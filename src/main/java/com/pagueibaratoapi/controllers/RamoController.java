package com.pagueibaratoapi.controllers;

import java.util.ArrayList;
import java.util.List;

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
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.pagueibaratoapi.models.requests.Ramo;
import com.pagueibaratoapi.models.responses.ResponseRamo;
import com.pagueibaratoapi.repository.RamoRepository;

@RestController
@RequestMapping("/ramo")
public class RamoController {
    
    private final RamoRepository ramoRepository;

    public RamoController(RamoRepository ramoRepository) {
        this.ramoRepository = ramoRepository;
    }

    @PostMapping
    public ResponseRamo criar(@RequestBody Ramo requestRamo){
        ResponseRamo responseRamo = new ResponseRamo(ramoRepository.save(requestRamo));

        responseRamo.add(
            linkTo(
                methodOn(RamoController.class).ler(responseRamo.getId())
            )
            .withSelfRel()
        );
        
        return responseRamo;
    }

    @GetMapping("/{id}")
    public ResponseRamo ler(@PathVariable(value = "id") Integer id){
        ResponseRamo responseRamo = new ResponseRamo(ramoRepository.findById(id).get());

        responseRamo.add(
            linkTo(
                methodOn(RamoController.class).listar(new Ramo())
            )
            .withRel("collection")
        );

        return responseRamo;
    }

    @GetMapping
    public List<ResponseRamo> listar(Ramo requestRamo){
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
    }

    @PatchMapping("/{id}")
    public ResponseRamo editar(@PathVariable(value = "id") Integer id, @RequestBody Ramo requestRamo){
        Ramo ramoAtual = ramoRepository.findById(id).get();

        if(requestRamo.getNome() != null)
            ramoAtual.setNome(requestRamo.getNome());

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
    }

    @PutMapping("/{id}")
    public ResponseRamo atualizar(@PathVariable(value = "id") Integer id, @RequestBody Ramo requestRamo){
        requestRamo.setId(id);

        ResponseRamo responseRamo = new ResponseRamo(ramoRepository.save(requestRamo));

        responseRamo.add(
            linkTo(
                methodOn(RamoController.class).ler(responseRamo.getId())
            )
            .withSelfRel()
        );

        return responseRamo;
    }

    @DeleteMapping("/{id}")
    public Object deletar(@PathVariable int id){
        ramoRepository.deleteById(id);
        
        return linkTo(
                    methodOn(RamoController.class).listar(new Ramo())
                )
                .withRel("collection"); 
    }
}
