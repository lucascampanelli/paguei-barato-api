package com.pagueibaratoapi.controllers;

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
    public String listar(
        @RequestParam(required = false) Object criadoPor,
        @RequestParam(required = false) Object ramoId,
        @RequestParam(required = false) String nome,
        @RequestParam(required = false) String logradouro,
        @RequestParam(required = false) String numero,
        @RequestParam(required = false) String complemento,
        @RequestParam(required = false) String bairro,
        @RequestParam(required = false) String cidade,
        @RequestParam(required = false) String uf,
        @RequestParam(required = false) String cep
    ){
        return "Bem vindo ao mercado! "+
            "criadoPor: "+criadoPor+
            "ramoId: "+ramoId+
            "nome: "+nome;
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
