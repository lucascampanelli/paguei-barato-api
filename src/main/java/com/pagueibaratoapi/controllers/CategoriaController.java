package com.pagueibaratoapi.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.pagueibaratoapi.models.exceptions.DadosConflitantesException;
import com.pagueibaratoapi.models.exceptions.DadosInvalidosException;
import com.pagueibaratoapi.models.requests.Categoria;
import com.pagueibaratoapi.models.responses.ResponseCategoria;
import com.pagueibaratoapi.repository.CategoriaRepository;
import com.pagueibaratoapi.utils.EditaRecurso;
import com.pagueibaratoapi.utils.Tratamento;

@RestController
@RequestMapping("/categoria")
public class CategoriaController {
    
    private final CategoriaRepository categoriaRepository;

    public CategoriaController(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @PostMapping
    public ResponseCategoria criar(@RequestBody Categoria requestCategoria) {
        try {
            Tratamento.validarCategoria(requestCategoria, false);

            if(categoriaRepository.existsByNomeIgnoreCase(requestCategoria.getNome()))
                throw new DadosConflitantesException("nome_existente");

            ResponseCategoria responseCategoria = new ResponseCategoria(categoriaRepository.save(requestCategoria));
    
            responseCategoria.add(
                linkTo(
                    methodOn(CategoriaController.class).ler(responseCategoria.getId())
                )
                .withSelfRel()
            );

            return responseCategoria;

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
    public ResponseCategoria ler(@PathVariable(value = "id") Integer id){
        try {
            
            ResponseCategoria responseCategoria = new ResponseCategoria(categoriaRepository.findById(id).get());
    
            if(responseCategoria != null){
                responseCategoria.add(
                    linkTo(
                        methodOn(CategoriaController.class).listar()
                    )
                    .withRel("collection")
                );
            }
    
            return responseCategoria;

        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    @GetMapping
    public List<ResponseCategoria> listar() {
        try {

            List<Categoria> categorias = categoriaRepository.findAll();
            List<ResponseCategoria> responseCategoria = new ArrayList<ResponseCategoria>();
    
            for(Categoria categoria : categorias){
                responseCategoria.add(
                    new ResponseCategoria(categoria)
                );
            }
    
            if(!responseCategoria.isEmpty()) {
                for(ResponseCategoria categoria : responseCategoria) {
                    categoria.add(
                        linkTo(
                            methodOn(CategoriaController.class).ler(categoria.getId())
                        )
                        .withSelfRel()
                    );
                }
            }
    
            return responseCategoria;

        } catch(NullPointerException  e) {
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch(UnsupportedOperationException e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch(Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    @PatchMapping("/{id}")
    public ResponseCategoria editar(@PathVariable int id, @RequestBody Categoria requestCategoria) {
        try {
            Tratamento.validarCategoria(requestCategoria, true);

            if(categoriaRepository.existsByNomeIgnoreCase(requestCategoria.getNome()))
                throw new DadosConflitantesException("nome_existente");

            Categoria categoriaAtual = categoriaRepository.findById(id).get();

            ResponseCategoria responseCategoria = new ResponseCategoria(
                                                                categoriaRepository.save(
                                                                    EditaRecurso.editarCategoria(
                                                                        categoriaAtual, 
                                                                        requestCategoria
                                                                    )
                                                                )
                                                        );
    
            responseCategoria.add(
                linkTo(
                    methodOn(CategoriaController.class).ler(responseCategoria.getId())
                )
                .withSelfRel()
            );
    
            return responseCategoria;

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
    public ResponseCategoria atualizar(@PathVariable int id, @RequestBody Categoria requestCategoria){
        try {

            if(categoriaRepository.existsByNomeIgnoreCase(requestCategoria.getNome()))
                throw new DadosConflitantesException("nome_existente");
            
            Tratamento.validarCategoria(requestCategoria, false);

            if(!categoriaRepository.existsById(id))
                throw new NoSuchElementException("nao_encontrado");
    
            requestCategoria.setId(id);
    
            ResponseCategoria responseCategoria = new ResponseCategoria(categoriaRepository.save(requestCategoria));
    
            responseCategoria.add(
                linkTo(
                    methodOn(CategoriaController.class).ler(responseCategoria.getId())
                )
                .withSelfRel()
            );
    
            return responseCategoria;

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
            
            if(!categoriaRepository.existsById(id))
                throw new NoSuchElementException("nao_encontrado");

            categoriaRepository.deleteById(id);
    
            return linkTo(
                        methodOn(CategoriaController.class).listar()
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
