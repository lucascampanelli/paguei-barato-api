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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.pagueibaratoapi.models.exceptions.DadosConflitantesException;
import com.pagueibaratoapi.models.exceptions.DadosInvalidosException;
import com.pagueibaratoapi.models.requests.Estoque;
import com.pagueibaratoapi.models.responses.ResponseEstoque;
import com.pagueibaratoapi.models.responses.ResponsePagina;
import com.pagueibaratoapi.repository.EstoqueRepository;
import com.pagueibaratoapi.repository.MercadoRepository;
import com.pagueibaratoapi.repository.ProdutoRepository;
import com.pagueibaratoapi.repository.UsuarioRepository;
import com.pagueibaratoapi.utils.PaginaUtils;
import com.pagueibaratoapi.utils.Tratamento;
import com.pagueibaratoapi.utils.TratamentoEstoque;

@RestController
@RequestMapping("/estoque")
public class EstoqueController {
    
    private final EstoqueRepository estoqueRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;
    private final MercadoRepository mercadoRepository;

    public EstoqueController(EstoqueRepository estoqueRepository, UsuarioRepository usuarioRepository, ProdutoRepository produtoRepository, MercadoRepository mercadoRepository) {
        this.estoqueRepository = estoqueRepository;
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
        this.mercadoRepository = mercadoRepository;
    }

    @PostMapping
    public ResponseEstoque criar(@RequestBody Estoque requestEstoque) {
        try {
            TratamentoEstoque.validar(requestEstoque, false);

            if(!usuarioRepository.existsById(requestEstoque.getCriadoPor()))
                throw new DadosInvalidosException("usuario_invalido");

            if(!produtoRepository.existsById(requestEstoque.getProdutoId()))
                throw new DadosInvalidosException("produto_invalido");

            if(!mercadoRepository.existsById(requestEstoque.getMercadoId()))
                throw new DadosInvalidosException("mercado_invalido");

            Estoque estoqueComparar = new Estoque();
            estoqueComparar.setProdutoId(requestEstoque.getProdutoId());
            estoqueComparar.setMercadoId(requestEstoque.getMercadoId());

            List<Estoque> estoquesSemelhantes = estoqueRepository.findAll(
                Example.of(estoqueComparar, ExampleMatcher
                                    .matching()
                                    .withIgnoreCase()
                                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)));

            if(estoquesSemelhantes.size() != 0)
                throw new DadosConflitantesException("estoque_existente");

            ResponseEstoque responseEstoque = new ResponseEstoque(estoqueRepository.save(requestEstoque));
    
            responseEstoque.add(
                linkTo(
                    methodOn(EstoqueController.class).ler(responseEstoque.getId())
                )
                .withSelfRel()
            );
    
            return responseEstoque;

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
    public ResponseEstoque ler(@PathVariable(value = "id") Integer id){
        try {

            ResponseEstoque responseEstoque = new ResponseEstoque(estoqueRepository.findById(id).get());
    
            if(responseEstoque != null){
                responseEstoque.add(
                    linkTo(
                        methodOn(EstoqueController.class).listar(new Estoque())
                    )
                    .withRel("collection")
                );
            }
    
            return responseEstoque;

        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    @GetMapping
    public List<ResponseEstoque> listar(Estoque requestEstoque){
        try {
            Tratamento.validarEstoque(requestEstoque, true);

            List<Estoque> estoques = estoqueRepository.findAll(
                Example.of(requestEstoque, ExampleMatcher
                                    .matching()
                                    .withIgnoreCase()
                                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)));
    
            List<ResponseEstoque> responseEstoque = new ArrayList<ResponseEstoque>();
    
            for(Estoque estoque : estoques){
                responseEstoque.add(new ResponseEstoque(estoque));
            }
    
            if(!responseEstoque.isEmpty()){
                for(ResponseEstoque estoque : responseEstoque){
                    estoque.add(
                        linkTo(
                            methodOn(EstoqueController.class).ler(estoque.getId())
                        )
                        .withSelfRel()
                    );
                }
            }
    
            return responseEstoque;

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
    public ResponsePagina listar(Estoque requestEstoque, @RequestParam(required = false, defaultValue = "0") Integer pagina, @RequestParam(required = false, defaultValue = "10") Integer limite){
        try {
            Tratamento.validarEstoque(requestEstoque, true);

            Page<Estoque> paginaEstoque = estoqueRepository.findAll(
                Example.of(requestEstoque, ExampleMatcher
                                    .matching()
                                    .withIgnoreCase()
                                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)),
                PageRequest.of(pagina, limite));
    
            List<ResponseEstoque> estoques = new ArrayList<ResponseEstoque>();
            
            for(Estoque estoque : paginaEstoque.getContent()){
                estoques.add(new ResponseEstoque(estoque));
            }
    
            ResponsePagina responseEstoque = PaginaUtils.criarResposta(pagina, limite, paginaEstoque, estoques);
            
            responseEstoque.add(
                linkTo(
                    methodOn(EstoqueController.class).listar(requestEstoque, 0, limite)
                )
                .withRel("first")
            );
    
            if(!paginaEstoque.isEmpty()){
                if(pagina > 0){
                    responseEstoque.add(
                        linkTo(
                            methodOn(EstoqueController.class).listar(requestEstoque, pagina-1, limite)
                        )
                        .withRel("previous")
                    );
                }
                if(pagina < paginaEstoque.getTotalPages()-1){
                    responseEstoque.add(
                        linkTo(
                            methodOn(EstoqueController.class).listar(requestEstoque, pagina+1, limite)
                        )
                        .withRel("next")
                    );
                }
                responseEstoque.add(
                    linkTo(
                        methodOn(EstoqueController.class).listar(requestEstoque, paginaEstoque.getTotalPages()-1, limite)
                    )
                    .withRel("last")
                );
    
                for(ResponseEstoque estoque : estoques){
                    estoque.add(
                        linkTo(
                            methodOn(EstoqueController.class).ler(estoque.getId())
                        )
                        .withSelfRel()
                    );
                }
            }
    
            return responseEstoque;

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

    @DeleteMapping("/{id}")
    public Object remover(@PathVariable int id){
        try {

            if(!estoqueRepository.existsById(id))
                throw new NoSuchElementException( "nao_encontrado");

            estoqueRepository.deleteById(id);
    
            return linkTo(
                        methodOn(EstoqueController.class).listar(new Estoque())
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
