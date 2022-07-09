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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.pagueibaratoapi.models.responses.ResponsePagina;
import com.pagueibaratoapi.models.responses.ResponseSugestao;
import com.pagueibaratoapi.models.exceptions.DadosInvalidosException;
import com.pagueibaratoapi.models.requests.Sugestao;
import com.pagueibaratoapi.repository.EstoqueRepository;
import com.pagueibaratoapi.repository.SugestaoRepository;
import com.pagueibaratoapi.repository.UsuarioRepository;
import com.pagueibaratoapi.utils.EditaRecurso;
import com.pagueibaratoapi.utils.PaginaUtils;
import com.pagueibaratoapi.utils.Tratamento;

@RestController
@RequestMapping("/sugestao")
public class SugestaoController {
    
    private final EstoqueRepository estoqueRepository;
    private final SugestaoRepository sugestaoRepository;
    private final UsuarioRepository usuarioRepository;

    public SugestaoController(  EstoqueRepository estoqueRepository,
                                SugestaoRepository sugestaoRepository,
                                UsuarioRepository usuarioRepository) {

        this.estoqueRepository = estoqueRepository;
        this.sugestaoRepository = sugestaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping
    public ResponseSugestao criar(@RequestBody Sugestao requestSugestao) {
        try {

            Tratamento.validarSugestao(requestSugestao, false);

            if(!estoqueRepository.existsById(requestSugestao.getEstoqueId()))
                throw new DadosInvalidosException("estoque_nao_encontrado");

            if(!usuarioRepository.existsById(requestSugestao.getCriadoPor()))
                throw new DadosInvalidosException("usuario_nao_encontrado");

            requestSugestao.setPreco(requestSugestao.getPreco() * 100);
    
            ResponseSugestao responseSugestao = new ResponseSugestao(sugestaoRepository.save(requestSugestao));
    
            responseSugestao.add(
                linkTo(
                    methodOn(SugestaoController.class).ler(responseSugestao.getId())
                )
                .withSelfRel()
            );
    
            responseSugestao.setPreco(responseSugestao.getPreco() / 100);
    
            return responseSugestao;

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
    public ResponseSugestao ler(@PathVariable(value = "id") Integer id){
        try {

            ResponseSugestao responseSugestao = new ResponseSugestao(sugestaoRepository.findById(id).get());
    
            if(responseSugestao != null){
                responseSugestao.setPreco(responseSugestao.getPreco() / 100);
                responseSugestao.add(
                    linkTo(
                        methodOn(SugestaoController.class).listar(new Sugestao())
                    )
                    .withRel("collection")
                );
            }
    
            return responseSugestao;

        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    @GetMapping
    public List<ResponseSugestao> listar(Sugestao requestSugestao) {
        try {

            Tratamento.validarSugestao(requestSugestao, true);

            List<Sugestao> sugestoes = sugestaoRepository.findAll(
                Example.of(requestSugestao, ExampleMatcher
                                    .matching()
                                    .withIgnoreCase()
                                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)));
    
            List<ResponseSugestao> responseSugestao = new ArrayList<ResponseSugestao>();
    
            for(Sugestao sugestao : sugestoes){
                responseSugestao.add(new ResponseSugestao(sugestao));
            }
    
            if(!responseSugestao.isEmpty()){
                for(ResponseSugestao sugestao : responseSugestao) {
                    sugestao.setPreco(sugestao.getPreco() / 100);
                    sugestao.add(
                        linkTo(
                            methodOn(SugestaoController.class).ler(sugestao.getId())
                        )
                        .withSelfRel()
                    );
                }
            }
    
            return responseSugestao;

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
    public ResponsePagina listar(Sugestao requestSugestao, @RequestParam(required = false, defaultValue = "0") Integer pagina, @RequestParam(required = false, defaultValue = "10") Integer limite) {
        try {
            
            Tratamento.validarSugestao(requestSugestao, true);

            Page<Sugestao> paginaSugestao = sugestaoRepository.findAll(
                Example.of(requestSugestao, ExampleMatcher
                                    .matching()
                                    .withIgnoreCase()
                                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)), 
                PageRequest.of(pagina, limite));
    
            List<ResponseSugestao> sugestoes = new ArrayList<ResponseSugestao>();
    
            ResponsePagina responsePagina = PaginaUtils.criarResposta(pagina, limite, paginaSugestao, sugestoes);
    
            for(Sugestao sugestao : paginaSugestao.getContent()){
                sugestoes.add(new ResponseSugestao(sugestao));
            }
    
            responsePagina.add(
                linkTo(
                    methodOn(SugestaoController.class).listar(requestSugestao, 0, limite)
                )
                .withRel("first")
            );
    
            if(!paginaSugestao.isEmpty()){
                if(pagina > 0){
                    responsePagina.add(
                        linkTo(
                            methodOn(SugestaoController.class).listar(requestSugestao, pagina-1, limite)
                        )
                        .withRel("previous")
                    );
                }
                if(pagina < paginaSugestao.getTotalPages()-1){
                    responsePagina.add(
                        linkTo(
                            methodOn(SugestaoController.class).listar(requestSugestao, pagina+1, limite)
                        )
                        .withRel("next")
                    );
                }
                responsePagina.add(
                    linkTo(
                        methodOn(SugestaoController.class).listar(requestSugestao, paginaSugestao.getTotalPages()-1, limite)
                    )
                    .withRel("last")
                );
    
                for(ResponseSugestao sugestao : sugestoes){
                    sugestao.setPreco(sugestao.getPreco() / 100);
                    sugestao.add(
                        linkTo(
                            methodOn(SugestaoController.class).ler(sugestao.getId())
                        )
                        .withSelfRel()
                    );
                }
            }
    
            return responsePagina;

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
    public ResponseSugestao editar(@PathVariable int id, @RequestBody Sugestao requestSugestao){
        try {

            Tratamento.validarSugestao(requestSugestao, true);

            Sugestao sugestaoAtual = sugestaoRepository.findById(id).get();
    
            ResponseSugestao responseSugestao = new ResponseSugestao(
                                                                sugestaoRepository.save(
                                                                    EditaRecurso.editarSugestao(sugestaoAtual, requestSugestao)
                                                                )
                                                            );

            responseSugestao.add(
                linkTo(
                    methodOn(SugestaoController.class).ler(responseSugestao.getId())
                )
                .withSelfRel()
            );
            
            responseSugestao.setPreco(responseSugestao.getPreco() / 100);
    
            return responseSugestao;

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
    public ResponseSugestao atualizar(@PathVariable int id, @RequestBody Sugestao requestSugestao){
        try {

            Tratamento.validarSugestao(requestSugestao, true);

            if(!estoqueRepository.existsById(requestSugestao.getEstoqueId()))
                throw new DadosInvalidosException("estoque_nao_encontrado");

            if(!usuarioRepository.existsById(requestSugestao.getCriadoPor()))
                throw new DadosInvalidosException("usuario_nao_encontrado");

            requestSugestao.setId(id);
            requestSugestao.setPreco(requestSugestao.getPreco() * 100);
    
            ResponseSugestao responseSugestao = new ResponseSugestao(sugestaoRepository.save(requestSugestao));
    
            responseSugestao.add(
                linkTo(
                    methodOn(SugestaoController.class).ler(responseSugestao.getId())
                )
                .withSelfRel()
            );
            
            responseSugestao.setPreco(responseSugestao.getPreco() / 100);
    
            return responseSugestao;

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

            if(!sugestaoRepository.existsById(id))
                throw new NoSuchElementException("nao_encontrado");

            sugestaoRepository.deleteById(id);
    
            return linkTo(
                        methodOn(SugestaoController.class).listar(new Sugestao())
                    ).withRel("collection");

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
