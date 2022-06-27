package com.pagueibaratoapi.controllers;

import java.util.List;

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

import com.pagueibaratoapi.models.Estoque;
import com.pagueibaratoapi.repository.EstoqueRepository;

@RestController
@RequestMapping("/estoque")
public class EstoqueController {
    
    private final EstoqueRepository estoqueRepository;

    public EstoqueController(EstoqueRepository estoqueRepository) {
        this.estoqueRepository = estoqueRepository;
    }

    @PostMapping
    public Estoque criar(@RequestBody Estoque requestEstoque) {
        return estoqueRepository.save(requestEstoque);
    }

    @GetMapping("/{id}")
    public Estoque ler(@PathVariable(value = "id") Integer id){
        return estoqueRepository.findById(id).get();
    }

    @GetMapping
    public List<Estoque> listar(Estoque requestEstoque){

        return estoqueRepository.findAll(
            Example.of(requestEstoque, ExampleMatcher
                                .matching()
                                .withIgnoreCase()
                                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)));
    }

    @GetMapping(params = {"pagina", "limite"})
    public Page<Estoque> listar(Estoque requestEstoque, @RequestParam(required = false, defaultValue = "0") Integer pagina, @RequestParam(required = false, defaultValue = "10") Integer limite){

        return estoqueRepository.findAll(
            Example.of(requestEstoque, ExampleMatcher
                                .matching()
                                .withIgnoreCase()
                                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)), PageRequest.of(pagina, limite));
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable int id){
        estoqueRepository.deleteById(id);
    }
    
}
