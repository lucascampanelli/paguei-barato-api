package com.pagueibaratoapi.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

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
import com.pagueibaratoapi.models.requests.Mercado;
import com.pagueibaratoapi.models.requests.Sugestao;
import com.pagueibaratoapi.models.responses.ResponseMercado;
import com.pagueibaratoapi.models.responses.ResponsePagina;
import com.pagueibaratoapi.models.responses.ResponseSugestao;
import com.pagueibaratoapi.repository.EstoqueRepository;
import com.pagueibaratoapi.repository.MercadoRepository;
import com.pagueibaratoapi.repository.RamoRepository;
import com.pagueibaratoapi.repository.SugestaoRepository;
import com.pagueibaratoapi.repository.UsuarioRepository;
import com.pagueibaratoapi.utils.EditaRecurso;
import com.pagueibaratoapi.utils.PaginaUtils;
import com.pagueibaratoapi.utils.Tratamento;

@RestController
@RequestMapping("/mercado")
public class MercadoController {
    
    private final MercadoRepository mercadoRepository;
    private final RamoRepository ramoRepository;
    private final UsuarioRepository usuarioRepository;
    private final SugestaoRepository sugestaoRepository;
    private final EstoqueRepository estoqueRepository;

    public MercadoController(MercadoRepository mercadoRepository, 
                             RamoRepository ramoRepository, 
                             UsuarioRepository usuarioRepository,
                             SugestaoRepository sugestaoRepository,
                             EstoqueRepository estoqueRepository) {

        this.mercadoRepository = mercadoRepository;
        this.ramoRepository = ramoRepository;
        this.usuarioRepository = usuarioRepository;
        this.sugestaoRepository = sugestaoRepository;
        this.estoqueRepository = estoqueRepository;
    }

    @PostMapping
    public ResponseMercado criar(@RequestBody Mercado requestMercado){
        try {

            Tratamento.validarMercado(requestMercado, false);

            if(!usuarioRepository.existsById(requestMercado.getCriadoPor()))
                throw new DadosInvalidosException("usuario_nao_encontrado");

            if(!ramoRepository.existsById(requestMercado.getRamoId()))
                throw new DadosInvalidosException("ramo_nao_encontrado");

            if(mercadoRepository.existsByNomeIgnoreCase(requestMercado.getNome()))
                throw new DadosConflitantesException("mercado_existente");

            if(mercadoRepository.findByEndereco(requestMercado.getLogradouro(), requestMercado.getNumero(), requestMercado.getComplemento(), requestMercado.getBairro(), requestMercado.getCidade(), requestMercado.getUf(), requestMercado.getCep()) != null)
                throw new DadosConflitantesException("mercado_existente");

            ResponseMercado responseMercado = new ResponseMercado(mercadoRepository.save(requestMercado));
    
            responseMercado.add(
                linkTo(
                    methodOn(MercadoController.class).ler(responseMercado.getId())
                )
                .withSelfRel()
            );
    
            return responseMercado;

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
    public ResponseMercado ler(@PathVariable(value = "id") Integer id){
        try {

            ResponseMercado responseMercado = new ResponseMercado(mercadoRepository.findById(id).get());
    
            if(responseMercado != null){
                responseMercado.add(
                    linkTo(
                        methodOn(MercadoController.class).listar(new Mercado())
                    )
                    .withRel("collection")
                );
            }
    
            return responseMercado;
            
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    @GetMapping("/{id}/produto/{produtoId}")
    public List<ResponseSugestao> ler(@PathVariable(value = "id") Integer id, @PathVariable(value = "produtoId") Integer produtoId){
        try {
            List<ResponseSugestao> responseSugestao = new ArrayList<ResponseSugestao>();

            // Buscando o registro do estoque do mercado, que associa o produto ao estoque do mercado
            Estoque estoque = estoqueRepository.findByProdutoIdAndMercadoId(produtoId, id);
            
            if(estoque == null)
                throw new NoSuchElementException("estoque_nao_encontrado");

            // Buscando todas as sugestões de preço do produto no mercado informado
            List<Sugestao> sugestoes = sugestaoRepository.findByEstoqueId(estoque.getId());

            if(sugestoes == null)
                throw new NoSuchElementException("sugestao_nao_encontrado");

            for(Sugestao sugestao : sugestoes){
                responseSugestao.add(new ResponseSugestao(sugestao));
            }

            for(ResponseSugestao sugestao : responseSugestao){
                sugestao.setPreco(sugestao.getPreco() / 100);

                sugestao.add(
                    linkTo(
                        methodOn(SugestaoController.class).ler(sugestao.getId())
                    )
                    .withSelfRel()
                );

                sugestao.add(
                    linkTo(
                        methodOn(MercadoController.class).listar(new Mercado())
                    )
                    .withRel("collection")
                );

                sugestao.add(
                    linkTo(
                        methodOn(ProdutoController.class).ler(produtoId)
                    )
                    .withRel("produto")
                );

                sugestao.add(
                    linkTo(
                        methodOn(MercadoController.class).ler(id)
                    )
                    .withRel("mercado")
                );

                sugestao.add(
                    linkTo(
                        methodOn(EstoqueController.class).ler(estoque.getId())
                    )
                    .withRel("estoque")
                );
            }
    
            return responseSugestao;
            
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(404, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    @GetMapping
    public List<ResponseMercado> listar(Mercado requestMercado){
        try {

            Tratamento.validarMercado(requestMercado, true);

            List<Mercado> mercados = mercadoRepository.findAll(
                                                Example.of(requestMercado, ExampleMatcher
                                                                    .matching()
                                                                    .withIgnoreCase()
                                                                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)));
            
            List<ResponseMercado> responseMercado = new ArrayList<ResponseMercado>();
    
            for(Mercado mercado : mercados){
                responseMercado.add(new ResponseMercado(mercado));
            }
    
            if(!responseMercado.isEmpty()){
                for(ResponseMercado mercado : responseMercado){
                    mercado.add(
                        linkTo(
                            methodOn(MercadoController.class).ler(mercado.getId())
                        )
                        .withSelfRel()
                    );
                }
            }
            
            return responseMercado;

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
    public ResponsePagina listar(Mercado requestMercado, @RequestParam(required = false, defaultValue = "0") Integer pagina, @RequestParam(required = false, defaultValue = "10") Integer limite){
        try {

            Tratamento.validarMercado(requestMercado, true);

            Page<Mercado> paginaMercado = mercadoRepository.findAll(
                                                                Example.of(requestMercado, ExampleMatcher
                                                                                    .matching()
                                                                                    .withIgnoreCase()
                                                                                    .withStringMatcher(
                                                                                        ExampleMatcher.StringMatcher.CONTAINING)), 
                                                                                        PageRequest.of(pagina, limite));
    
            
            List<ResponseMercado> mercados = new ArrayList<ResponseMercado>();
    
            for(Mercado mercado : paginaMercado.getContent()){
                mercados.add(new ResponseMercado(mercado));
            }
    
            ResponsePagina responseMercado = PaginaUtils.criarResposta(pagina, limite, paginaMercado, mercados);
    
            responseMercado.add(
                linkTo(
                    methodOn(MercadoController.class).listar(requestMercado, 0, limite)
                )
                .withRel("first")
            );
    
            if(!paginaMercado.isEmpty()){
                if(pagina > 0){
                    responseMercado.add(
                        linkTo(
                            methodOn(MercadoController.class).listar(requestMercado, pagina-1, limite)
                        )
                        .withRel("previous")
                    );
                }
                if(pagina < paginaMercado.getTotalPages()-1){
                    responseMercado.add(
                        linkTo(
                            methodOn(MercadoController.class).listar(requestMercado, pagina+1, limite)
                        )
                        .withRel("next")
                    );
                }
                responseMercado.add(
                    linkTo(
                        methodOn(MercadoController.class).listar(requestMercado, paginaMercado.getTotalPages()-1, limite)
                    )
                    .withRel("last")
                );
    
                for(ResponseMercado mercado : mercados){
                    mercado.add(
                        linkTo(
                            methodOn(MercadoController.class).ler(mercado.getId())
                        )
                        .withSelfRel()
                    );
                }
            }
            
            return responseMercado;

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
    public ResponseMercado editar(@PathVariable int id, @RequestBody Mercado requestMercado){
        try {

            Tratamento.validarMercado(requestMercado, true);

            if(mercadoRepository.findByEndereco(requestMercado.getLogradouro(), requestMercado.getNumero(), requestMercado.getComplemento(), requestMercado.getBairro(), requestMercado.getCidade(), requestMercado.getUf(), requestMercado.getCep()) != null)
                throw new DadosConflitantesException("mercado_existente");

            Mercado mercadoAtual = mercadoRepository.findById(id).get();
            
            if(!ramoRepository.existsById(requestMercado.getRamoId()))
                    throw new DadosInvalidosException("ramo_nao_encontrado");

            if(mercadoRepository.existsByNomeIgnoreCase(requestMercado.getNome()))
                throw new DadosConflitantesException("mercado_existente");
    
            ResponseMercado responseMercado = new ResponseMercado(
                                                            mercadoRepository.save(
                                                                EditaRecurso.editarMercado(mercadoAtual, requestMercado)
                                                            )
                                                        );
            
            responseMercado.add(
                linkTo(
                    methodOn(MercadoController.class).ler(responseMercado.getId())
                )
                .withSelfRel()
            );
    
            return responseMercado;

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
    public ResponseMercado atualizar(@PathVariable int id, @RequestBody Mercado requestMercado){
        try {

            Tratamento.validarMercado(requestMercado, false);

            if(!mercadoRepository.existsById(id))
                throw new NoSuchElementException("nao_encontrado");

            if(!usuarioRepository.existsById(requestMercado.getCriadoPor()))
                throw new DadosInvalidosException("usuario_nao_encontrado");

            if(!ramoRepository.existsById(requestMercado.getRamoId()))
                throw new DadosInvalidosException("ramo_nao_encontrado");

            if(mercadoRepository.existsByNomeIgnoreCase(requestMercado.getNome()))
                throw new DadosConflitantesException("mercado_existente");

            if(mercadoRepository.findByEndereco(requestMercado.getLogradouro(), requestMercado.getNumero(), requestMercado.getComplemento(), requestMercado.getBairro(), requestMercado.getCidade(), requestMercado.getUf(), requestMercado.getCep()) != null)
                throw new DadosConflitantesException("mercado_existente");

            requestMercado.setId(id);

            ResponseMercado responseMercado = new ResponseMercado(mercadoRepository.save(requestMercado));
            
            responseMercado.add(
                linkTo(
                    methodOn(MercadoController.class).ler(responseMercado.getId())
                )
                .withSelfRel()
            );
    
            return responseMercado;

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
    public Object remover(@PathVariable int id){
        try {
            
            if(!mercadoRepository.existsById(id))
                throw new NoSuchElementException( "nao_encontrado");

            mercadoRepository.deleteById(id);
    
            return linkTo(
                        methodOn(MercadoController.class).listar(new Mercado())
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
