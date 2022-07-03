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

import com.pagueibaratoapi.models.requests.Sugestao;
import com.pagueibaratoapi.repository.SugestaoRepository;

@RestController
@RequestMapping("/sugestao")
public class SugestaoController {
    
    private final SugestaoRepository sugestaoRepository;

    public SugestaoController(SugestaoRepository sugestaoRepository) {
        this.sugestaoRepository = sugestaoRepository;
    }

    @PostMapping
    public Sugestao criar(@RequestBody Sugestao requestSugestao) {
        requestSugestao.setPreco(requestSugestao.getPreco() * 100);

        Sugestao responseSugestao = sugestaoRepository.save(requestSugestao);

        responseSugestao.setPreco(responseSugestao.getPreco() / 100);

        return responseSugestao;
    }

    @GetMapping("/{id}")
    public Sugestao ler(@PathVariable(value = "id") Integer id){
        return sugestaoRepository.findById(id).get();
    }

    @GetMapping
    public List<Sugestao> listar(Sugestao requestSugestao) {
        List<Sugestao> responseSugestao = sugestaoRepository.findAll(
            Example.of(requestSugestao, ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)));

        for(Sugestao item : responseSugestao) {
            item.setPreco(item.getPreco() / 100);
        }

        return responseSugestao;
    }

    @GetMapping(params = {"pagina", "limite"})
    public Page<Sugestao> listar(Sugestao requestSugestao, @RequestParam(required = false, defaultValue = "0") Integer pagina, @RequestParam(required = false, defaultValue = "10") Integer limite) {
        Page<Sugestao> responseSugestao = sugestaoRepository.findAll(
            Example.of(requestSugestao, ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)), PageRequest.of(pagina, limite));

        for(Sugestao item : responseSugestao.getContent()) {
            item.setPreco(item.getPreco() / 100);
        }

        return responseSugestao;
    }

    @PatchMapping("/{id}")
    public Sugestao editar(@PathVariable int id, @RequestBody Sugestao requestSugestao){
        Sugestao sugestaoAtual = sugestaoRepository.findById(id).get();

        if(requestSugestao.getPreco() != null)
            sugestaoAtual.setPreco(requestSugestao.getPreco());

        return sugestaoRepository.save(sugestaoAtual);
    }

    @PutMapping("/{id}")
    public Sugestao atualizar(@PathVariable int id, @RequestBody Sugestao requestSugestao){
        requestSugestao.setId(id);
        return sugestaoRepository.save(requestSugestao);
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable int id){
        sugestaoRepository.deleteById(id);
    }
}
