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
import com.pagueibaratoapi.models.Mercado;
import com.pagueibaratoapi.repository.MercadoRepository;

@RestController
@RequestMapping("/mercado")
public class MercadoController {
    
    private final MercadoRepository mercadoRepository;

    public MercadoController(MercadoRepository mercadoRepository) {
        this.mercadoRepository = mercadoRepository;
    }

    @PostMapping
    public Mercado criar(@RequestBody Mercado mercado){
        return mercadoRepository.save(mercado);
    }

    @GetMapping("/{id}")
    public Mercado ler(@PathVariable(value = "id") Integer id){
        return mercadoRepository.findById(id).get();
    }

    @GetMapping
    public List<Mercado> listar(Mercado requestMercado){

        return mercadoRepository.findAll(
            Example.of(requestMercado, ExampleMatcher
                                .matching()
                                .withIgnoreCase()
                                .withStringMatcher(ExampleMatcher.StringMatcher.DEFAULT)));
    }

    @GetMapping(params = {"pagina", "limite"})
    public Page<Mercado> listar(Mercado requestMercado, @RequestParam(required = false, defaultValue = "0") Integer pagina, @RequestParam(required = false, defaultValue = "10") Integer limite){

        return mercadoRepository.findAll(
            Example.of(requestMercado, ExampleMatcher
                                .matching()
                                .withIgnoreCase()
                                .withStringMatcher(ExampleMatcher.StringMatcher.DEFAULT)), PageRequest.of(pagina, limite));
    }

    @PatchMapping("/{id}")
    public void editar(@PathVariable int id){

    }

    @PutMapping
    public void atualizar(){

    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable int id){

    }
}
