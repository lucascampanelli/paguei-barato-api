package com.pagueibaratoapi.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pagueibaratoapi.models.Sugestao;
import com.pagueibaratoapi.repository.SugestaoRepository;

@RestController
@RequestMapping("/sugestao")
public class SugestaoController {
    
    private final SugestaoRepository sugestaoRepository;

    public SugestaoController(SugestaoRepository sugestaoRepository) {
        this.sugestaoRepository = sugestaoRepository;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public Sugestao criar(@RequestBody Sugestao sugestao) {
        sugestao.setPreco(sugestao.getPreco() * 100);

        Sugestao responseSugestao = sugestaoRepository.save(sugestao);

        responseSugestao.setPreco(responseSugestao.getPreco() / 100);

        return responseSugestao;
    }

    @GetMapping(produces = "application/json")
    public List<Sugestao> listar() {
        List<Sugestao> responseSugestao = sugestaoRepository.findAll();

        for(Sugestao item : responseSugestao) {
            item.setPreco(item.getPreco() / 100);
        }

        return responseSugestao;
    }
}
