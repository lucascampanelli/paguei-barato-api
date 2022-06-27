package com.pagueibaratoapi.controllers;

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

import com.pagueibaratoapi.models.Ramo;
import com.pagueibaratoapi.repository.RamoRepository;

@RestController
@RequestMapping("/ramo")
public class RamoController {
    
    private final RamoRepository ramoRepository;

    public RamoController(RamoRepository ramoRepository) {
        this.ramoRepository = ramoRepository;
    }

    @PostMapping
    public Ramo criar(@RequestBody Ramo requestRamo){
        return ramoRepository.save(requestRamo);
    }

    @GetMapping("/{id}")
    public Ramo ler(@PathVariable(value = "id") Integer id){
        return ramoRepository.findById(id).get();
    }

    @GetMapping
    public List<Ramo> listar(Ramo requestRamo){

        return ramoRepository.findAll(
            Example.of(requestRamo, ExampleMatcher
                                .matching()
                                .withIgnoreCase()
                                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)));

    }

    @PatchMapping("/{id}")
    public Ramo editar(@PathVariable(value = "id") Integer id, @RequestBody Ramo requestRamo){
        Ramo ramoAtual = ramoRepository.findById(id).get();

        if(requestRamo.getNome() != null)
            ramoAtual.setNome(requestRamo.getNome());

        if(requestRamo.getDescricao() != null)
            ramoAtual.setDescricao(requestRamo.getDescricao());

        return ramoRepository.save(ramoAtual);
    }

    @PutMapping("/{id}")
    public Ramo atualizar(@PathVariable(value = "id") Integer id, @RequestBody Ramo requestRamo){
        requestRamo.setId(id);
        return ramoRepository.save(requestRamo);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable int id){
        ramoRepository.deleteById(id);
    }
}
