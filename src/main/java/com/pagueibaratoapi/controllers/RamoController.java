package com.pagueibaratoapi.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.pagueibaratoapi.models.exceptions.DadosConflitantesException;
import com.pagueibaratoapi.models.exceptions.DadosInvalidosException;
import com.pagueibaratoapi.models.requests.Ramo;
import com.pagueibaratoapi.models.responses.ResponseRamo;
import com.pagueibaratoapi.repository.RamoRepository;
import com.pagueibaratoapi.utils.Tratamento;

@RestController
@RequestMapping("/ramo")
public class RamoController {
    
    private final RamoRepository ramoRepository;

    public RamoController(RamoRepository ramoRepository) {
        this.ramoRepository = ramoRepository;
    }

    @PostMapping
    public ResponseRamo criar(@RequestBody Ramo requestRamo){
        try {

            Tratamento.validarRamo(requestRamo, false);

            if(ramoRepository.existsByNomeIgnoreCase(requestRamo.getNome()))
                throw new DadosConflitantesException("ramo_existente");

            ResponseRamo responseRamo = new ResponseRamo(ramoRepository.save(requestRamo));
    
            responseRamo.add(
                linkTo(
                    methodOn(RamoController.class).ler(responseRamo.getId())
                )
                .withSelfRel()
            );
            
            return responseRamo;

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
    public ResponseRamo ler(@PathVariable(value = "id") Integer id){
        try {
        
            ResponseRamo responseRamo = new ResponseRamo(ramoRepository.findById(id).get());
    
            responseRamo.add(
                linkTo(
                    methodOn(RamoController.class).listar(new Ramo())
                )
                .withRel("collection")
            );
    
            return responseRamo;
            
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    @GetMapping
    public List<ResponseRamo> listar(Ramo requestRamo){
        try {

            Tratamento.validarRamo(requestRamo, true);

            List<Ramo> ramos = ramoRepository.findAll(
                                                        Example.of(requestRamo, ExampleMatcher
                                                                                    .matching()
                                                                                    .withIgnoreCase()
                                                                                    .withStringMatcher(
                                                                                        ExampleMatcher.StringMatcher.CONTAINING
                                                                                    )
                                                                )
                                                            );
    
            List<ResponseRamo> responseRamo = new ArrayList<ResponseRamo>();
    
            for(Ramo ramo : ramos){
                responseRamo.add(new ResponseRamo(ramo));
            }
    
            if(!responseRamo.isEmpty()){
                for(ResponseRamo ramo : responseRamo){
                    ramo.add(
                        linkTo(
                            methodOn(RamoController.class).ler(ramo.getId())
                        )
                        .withSelfRel()
                    );
                }
            }
    
            return responseRamo;

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

    @PatchMapping("/{id}")
    public ResponseRamo editar(@PathVariable(value = "id") Integer id, @RequestBody Ramo requestRamo){
        try {

            Tratamento.validarRamo(requestRamo, true);

            Ramo ramoAtual = ramoRepository.findById(id).get();
    
            if(requestRamo.getNome() != null){
                if(ramoRepository.existsByNomeIgnoreCase(requestRamo.getNome()))
                    throw new DadosConflitantesException("ramo_existente");

                ramoAtual.setNome(requestRamo.getNome());
            }
    
            if(requestRamo.getDescricao() != null)
                ramoAtual.setDescricao(requestRamo.getDescricao());
    
            ResponseRamo responseRamo = new ResponseRamo(ramoRepository.save(ramoAtual));
    
            responseRamo.add(
                linkTo(
                    methodOn(RamoController.class).ler(responseRamo.getId())
                )
                .withSelfRel()
            );
    
            return responseRamo;

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
    public ResponseRamo atualizar(@PathVariable(value = "id") Integer id, @RequestBody Ramo requestRamo){
        try {

            Tratamento.validarRamo(requestRamo, false);

            if(ramoRepository.existsByNomeIgnoreCase(requestRamo.getNome()))
                throw new DadosConflitantesException("ramo_existente");

            requestRamo.setId(id);
    
            ResponseRamo responseRamo = new ResponseRamo(ramoRepository.save(requestRamo));
    
            responseRamo.add(
                linkTo(
                    methodOn(RamoController.class).ler(responseRamo.getId())
                )
                .withSelfRel()
            );
    
            return responseRamo;

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

            if(!ramoRepository.existsById(id))
                throw new NoSuchElementException("nao_encontrado");

            ramoRepository.deleteById(id);
            
            return linkTo(
                        methodOn(RamoController.class).listar(new Ramo())
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
