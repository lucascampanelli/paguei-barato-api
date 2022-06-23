package com.pagueibaratoapi.controllers;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mercado")
public class MercadoController {
    
    @PostMapping
    public String create(){
        return "milu";
    }

    @GetMapping
    public String read(){
        return "Milu muito fofo";
    }

    @GetMapping("/{nome}")
    public String read(@PathVariable Object nome){
        return "O "+nome+" é muito fofo";
    }

    @PatchMapping
    public void update(){

    }

    @DeleteMapping
    public void delete(){

    }
}
