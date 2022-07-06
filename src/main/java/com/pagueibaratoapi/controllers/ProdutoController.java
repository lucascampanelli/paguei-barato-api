package com.pagueibaratoapi.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.pagueibaratoapi.models.exceptions.DadosConflitantesException;
import com.pagueibaratoapi.models.exceptions.DadosInvalidosException;
import com.pagueibaratoapi.models.requests.Estoque;
import com.pagueibaratoapi.models.requests.Produto;
import com.pagueibaratoapi.models.requests.Sugestao;
import com.pagueibaratoapi.models.responses.ResponseLevantamentoProduto;
import com.pagueibaratoapi.models.responses.ResponsePagina;
import com.pagueibaratoapi.models.responses.ResponseProduto;
import com.pagueibaratoapi.repository.CategoriaRepository;
import com.pagueibaratoapi.repository.EstoqueRepository;
import com.pagueibaratoapi.repository.ProdutoRepository;
import com.pagueibaratoapi.repository.SugestaoRepository;
import com.pagueibaratoapi.repository.UsuarioRepository;
import com.pagueibaratoapi.utils.PaginaUtils;
import com.pagueibaratoapi.utils.Tratamento;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/produto")
public class ProdutoController {
    
    private final CategoriaRepository categoriaRepository;
    private final ProdutoRepository produtoRepository;
    private final EstoqueRepository estoqueRepository;
    private final SugestaoRepository sugestaoRepository;
    private final UsuarioRepository usuarioRepository;

    public ProdutoController(CategoriaRepository categoriaRepository,
                             ProdutoRepository produtoRepository, 
                             EstoqueRepository estoqueRepository, 
                             SugestaoRepository sugestaoRepository,
                             UsuarioRepository usuarioRepository) {

        this.categoriaRepository = categoriaRepository;
        this.produtoRepository = produtoRepository;
        this.estoqueRepository = estoqueRepository;
        this.sugestaoRepository = sugestaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping
    public ResponseProduto criar(@RequestBody Produto requestProduto){
        try {

            Tratamento.validarProduto(requestProduto, false);

            if(!usuarioRepository.existsById(requestProduto.getCriadoPor()))
                throw new DadosInvalidosException("usuario_nao_encontrado");

            if(!categoriaRepository.existsById(requestProduto.getCategoriaId()))
                throw new DadosInvalidosException("categoria_nao_encontrado");

            if(produtoRepository.findByCaracteristicas(requestProduto.getNome(), requestProduto.getMarca(), requestProduto.getTamanho(), requestProduto.getCor()) != null)
                throw new DadosConflitantesException("produto_existente");

            // Criando uma nova instância do produto para tratar o nome dele e criá-lo no banco
            Produto produtoTratado = requestProduto;
    
            // Pegando cada palavra do nome do produto separado por espaço e em minúsculas
            String[] nomeProduto = requestProduto.getNome().toLowerCase().split(" ");
    
            // Percorrendo cada palavra do nome do produto
            for(int i = 0; i < nomeProduto.length; i++){
                // Transformando a palavra atual em uma array
                char[] palavraArr = nomeProduto[i].toCharArray();
    
                // Transformando a primeira letra da palavra em maiúscula
                palavraArr[0] = nomeProduto[i].toUpperCase().charAt(0);
    
                // Reescreve a palavra atual com a primeira letra tratada
                nomeProduto[i] = String.valueOf(palavraArr);
    
                // Se for a primeira palavra sendo tratada, substitui o nome do produto pelo nome tratado
                if(i < 1)
                    produtoTratado.setNome(nomeProduto[i]);
                // Se não, concatena a palavra atual ao nome do produto
                else
                    produtoTratado.setNome(produtoTratado.getNome() + " " + nomeProduto[i]);
            }
    
            ResponseProduto responseProduto = new ResponseProduto(produtoRepository.save(produtoTratado));
    
            responseProduto.add(
                linkTo(
                    methodOn(ProdutoController.class).ler(responseProduto.getId())
                )
                .withSelfRel()
            );
    
            return responseProduto;

        } catch (DadosConflitantesException e) {
            throw new ResponseStatusException(409, e.getMessage(), e);
        } catch (DadosInvalidosException e) {
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(500, "erro_insercao", e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    @GetMapping("/{id}")
    public ResponseProduto ler(@PathVariable(value = "id") Integer id){
        try {

            ResponseProduto responseProduto = new ResponseProduto(produtoRepository.findById(id).get());
            
            if(responseProduto != null){
                responseProduto.add(
                    linkTo(
                        methodOn(ProdutoController.class).listar(new Produto())
                    )
                    .withRel("collection")
                );
            }
            
            return responseProduto;
            
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    @GetMapping("/{id}/levantamento")
    public ResponseLevantamentoProduto levantamento(@PathVariable(value = "id") Integer id){
        try {

            ResponseLevantamentoProduto responseProduto = new ResponseLevantamentoProduto(produtoRepository.findById(id).get());
            
            List<Estoque> estoques = estoqueRepository.findByProdutoId(id);
    
            float somaPreco = 0.0f;
            int quantidadeSugestoes = 0;
    
            if(estoques != null){
                for(Estoque estoque : estoques){
                    // Buscando as sugestões do produto no mercado atual
                    List<Sugestao> sugestoes = sugestaoRepository.findByEstoqueId(estoque.getId());
                    
                    if(sugestoes != null){
                        for(Sugestao sugestao : sugestoes){
                            quantidadeSugestoes++;
                            somaPreco += (sugestao.getPreco()/100);
            
                            if(responseProduto.getDataUltimaSugestao() == null)
                                responseProduto.setDataUltimaSugestao(sugestao.getTimestamp());
                            else if(responseProduto.getDataUltimaSugestao().before(sugestao.getTimestamp()))
                                responseProduto.setDataUltimaSugestao(sugestao.getTimestamp());
            
                            if(responseProduto.getMaiorPreco() == 0.0f)
                                responseProduto.setMaiorPreco(sugestao.getPreco()/100);
                            else if(responseProduto.getMaiorPreco() < sugestao.getPreco())
                                responseProduto.setMaiorPreco(sugestao.getPreco()/100);
            
                            if(responseProduto.getMenorPreco() == 0.0f)
                                responseProduto.setMenorPreco(sugestao.getPreco()/100);
                            else if(responseProduto.getMenorPreco() > sugestao.getPreco())
                                responseProduto.setMenorPreco(sugestao.getPreco()/100);
                        }
                    }
                }
            }
    
            responseProduto.setQuantidadeSugestoes(quantidadeSugestoes);
            responseProduto.setPrecoMedio(quantidadeSugestoes > 0 ? somaPreco / quantidadeSugestoes : 0.0f);
    
            if(responseProduto != null){
                responseProduto.add(
                    linkTo(
                        methodOn(ProdutoController.class).listar(new Produto())
                    )
                    .withRel("collection")
                );
            }
            
            return responseProduto;

        } catch(IllegalArgumentException e) {
            throw new ResponseStatusException(500, "erro_interno", e);
        } catch(NullPointerException  e) {
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch(UnsupportedOperationException e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch(Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    @GetMapping
    public List<ResponseProduto> listar(Produto requestProduto){
        try {

            Tratamento.validarProduto(requestProduto, true);

            List<Produto> produtos = produtoRepository.findAll(
                Example.of(requestProduto, ExampleMatcher
                                    .matching()
                                    .withIgnoreCase()
                                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)));
    
            List<ResponseProduto> responseProduto = new ArrayList<ResponseProduto>();
    
            for(Produto produto : produtos){
                responseProduto.add(new ResponseProduto(produto));
            }
    
            if(responseProduto != null){
                for(ResponseProduto produto : responseProduto){
                    produto.add(
                        linkTo(
                            methodOn(ProdutoController.class).ler(produto.getId())
                        )
                        .withSelfRel()
                    );
                }
            }
    
            return responseProduto;
        } catch(DadosInvalidosException e) {
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch(NullPointerException  e) {
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch(UnsupportedOperationException e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch(Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    @GetMapping(params = {"pagina", "limite"})
    public ResponsePagina listar(Produto requestProduto, @RequestParam(required = false, defaultValue = "0") Integer pagina, @RequestParam(required = false, defaultValue = "10") Integer limite){
        try {

            Tratamento.validarProduto(requestProduto, true);

            Page<Produto> paginaProduto = produtoRepository.findAll(
                Example.of(requestProduto, ExampleMatcher
                                    .matching()
                                    .withIgnoreCase()
                                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)), 
                PageRequest.of(pagina, limite));
    
            List<ResponseProduto> produtos = new ArrayList<ResponseProduto>();
    
            ResponsePagina responseProduto = PaginaUtils.criarResposta(pagina, limite, paginaProduto, produtos);
    
            for(Produto produto : paginaProduto.getContent()){
                produtos.add(new ResponseProduto(produto));
            }
    
            responseProduto.add(
                linkTo(
                    methodOn(ProdutoController.class).listar(requestProduto, 0, limite)
                )
                .withRel("first")
            );
    
            if(!paginaProduto.isEmpty()){
                if(pagina > 0){
                    responseProduto.add(
                        linkTo(
                            methodOn(ProdutoController.class).listar(requestProduto, pagina-1, limite)
                        )
                        .withRel("previous")
                    );
                }
                if(pagina < paginaProduto.getTotalPages()-1){
                    responseProduto.add(
                        linkTo(
                            methodOn(ProdutoController.class).listar(requestProduto, pagina+1, limite)
                        )
                        .withRel("next")
                    );
                }
                responseProduto.add(
                    linkTo(
                        methodOn(ProdutoController.class).listar(requestProduto, paginaProduto.getTotalPages()-1, limite)
                    )
                    .withRel("last")
                );
    
                for(ResponseProduto produto : produtos){
                    produto.add(
                        linkTo(
                            methodOn(ProdutoController.class).ler(produto.getId())
                        )
                        .withSelfRel()
                    );
                }
            }
    
            return responseProduto;

        } catch (DadosInvalidosException e) {
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (NullPointerException e) {
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    @PatchMapping("/{id}")
    public ResponseProduto editar(@PathVariable int id, @RequestBody Produto requestProduto){
        try {

            Tratamento.validarProduto(requestProduto, true);

            if(produtoRepository.findByCaracteristicas(requestProduto.getNome(), requestProduto.getMarca(), requestProduto.getTamanho(), requestProduto.getCor()) != null)
                throw new DadosConflitantesException("produto_existente");

            Produto produtoAtual = produtoRepository.findById(id).get();
    
            if(requestProduto.getNome() != null)
                produtoAtual.setNome(requestProduto.getNome());
    
            if(requestProduto.getMarca() != null)
                produtoAtual.setMarca(requestProduto.getMarca());
            
            if(requestProduto.getTamanho() != null)
                produtoAtual.setTamanho(requestProduto.getTamanho());
    
            if(requestProduto.getCor() != null){
                if(requestProduto.getCor() == "")
                    produtoAtual.setCor(null);
                else
                    produtoAtual.setCor(requestProduto.getCor());
            }
    
            if(requestProduto.getCategoriaId() != null){
                if(!categoriaRepository.existsById(requestProduto.getCategoriaId()))
                    throw new DadosInvalidosException("categoria_nao_encontrado");

                produtoAtual.setCategoriaId(requestProduto.getCategoriaId());
            }
    
            ResponseProduto responseProduto = new ResponseProduto(produtoRepository.save(produtoAtual));
    
            responseProduto.add(
                linkTo(
                    methodOn(ProdutoController.class).ler(responseProduto.getId())
                )
                .withSelfRel()
            );
    
            return responseProduto;

        } catch (DadosConflitantesException e) {
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (DadosInvalidosException e) {
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(500, "erro_insercao", e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    @PutMapping("/{id}")
    public ResponseProduto atualizar(@PathVariable int id, @RequestBody Produto requestProduto){
        try {

            Tratamento.validarProduto(requestProduto, false);

            if(!usuarioRepository.existsById(requestProduto.getCriadoPor()))
                throw new DadosInvalidosException("usuario_nao_encontrado");

            if(!categoriaRepository.existsById(requestProduto.getCategoriaId()))
                throw new DadosInvalidosException("categoria_nao_encontrado");

            if(produtoRepository.findByCaracteristicas(requestProduto.getNome(), requestProduto.getMarca(), requestProduto.getTamanho(), requestProduto.getCor()) != null)
                throw new DadosConflitantesException("produto_existente");

            requestProduto.setId(id);
    
            ResponseProduto responseProduto = new ResponseProduto(produtoRepository.save(requestProduto));
    
            responseProduto.add(
                linkTo(
                    methodOn(ProdutoController.class).ler(responseProduto.getId())
                )
                .withSelfRel()
            );
    
            return responseProduto;

        } catch (DadosConflitantesException e) {
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (DadosInvalidosException e) {
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(404, e.getMessage(), e);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(500, "erro_insercao", e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    @DeleteMapping("/{id}")
    public Object deletar(@PathVariable int id){
        try {

            if(!produtoRepository.existsById(id))
                throw new NoSuchElementException("nao_encontrado");

            produtoRepository.deleteById(id);
    
            return linkTo(
                        methodOn(ProdutoController.class).listar(new Produto())
                    )
                    .withRel("collection");

        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(404, e.getMessage(), e);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(500, "erro_remocao", e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

}
