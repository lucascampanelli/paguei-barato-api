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
    public Mercado criar(@RequestBody Mercado requestMercado){
        return mercadoRepository.save(requestMercado);
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
                                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)));
    }

    @GetMapping(params = {"pagina", "limite"})
    public Page<Mercado> listar(Mercado requestMercado, @RequestParam(required = false, defaultValue = "0") Integer pagina, @RequestParam(required = false, defaultValue = "10") Integer limite){

        return mercadoRepository.findAll(
            Example.of(requestMercado, ExampleMatcher
                                .matching()
                                .withIgnoreCase()
                                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)), PageRequest.of(pagina, limite));
    }

    @PatchMapping("/{id}")
    public Mercado editar(@PathVariable int id, @RequestBody Mercado requestMercado){
        Mercado mercadoAtual = mercadoRepository.findById(id).get();
        
        if(requestMercado.getRamoId() != null){
            mercadoAtual.setRamoId(requestMercado.getRamoId());
        }
        if(requestMercado.getNome() != null){
            mercadoAtual.setNome(requestMercado.getNome());
        }
        if(requestMercado.getLogradouro() != null){
            mercadoAtual.setLogradouro(requestMercado.getLogradouro());
        }
        if(requestMercado.getNumero() != null){
            mercadoAtual.setNumero(requestMercado.getNumero());
        }
        if(requestMercado.getComplemento() != null){
            if(requestMercado.getComplemento().trim().isEmpty()){
                mercadoAtual.setComplemento(null);
            } else {
                mercadoAtual.setComplemento(requestMercado.getComplemento());
            }
        }
        if(requestMercado.getBairro() != null){
            mercadoAtual.setBairro(requestMercado.getBairro());
        }
        if(requestMercado.getCidade() != null){
            mercadoAtual.setCidade(requestMercado.getCidade());
        }
        if(requestMercado.getUf() != null){
            mercadoAtual.setUf(requestMercado.getUf());
        }
        if(requestMercado.getCep() != null){
            mercadoAtual.setCep(requestMercado.getCep());
        }

        return mercadoRepository.save(mercadoAtual);
    }

    @PutMapping("/{id}")
    public void atualizar(@PathVariable int id, @RequestBody Mercado requestMercado){
        requestMercado.setId(id);
        mercadoRepository.save(requestMercado);
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable int id){
        mercadoRepository.deleteById(id);
    }
}
