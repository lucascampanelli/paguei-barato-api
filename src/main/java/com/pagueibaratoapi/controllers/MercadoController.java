package com.pagueibaratoapi.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pagueibaratoapi.models.Mercado;

@RestController
@RequestMapping("/mercado")
public class MercadoController {
    
    @PostMapping(consumes = "application/json", produces = "application/json")
    public Mercado criar(@RequestBody Mercado mercado){
        return mercado;
    }

    @GetMapping("/{nome}")
    public String ler(@PathVariable Object nome){
        return "Olá, "+nome+"!";
    }

    @GetMapping
    public String listar(){
        return "Bem vindo ao mercado!";
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
