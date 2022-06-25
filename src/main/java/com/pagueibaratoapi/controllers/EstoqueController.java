package com.pagueibaratoapi.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping(produces = "application/json")
    public List<Estoque> listar() {
        return estoqueRepository.findAll();
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public Estoque criar(@RequestBody Estoque estoque) {
        return estoqueRepository.save(estoque);
    }
    
}
