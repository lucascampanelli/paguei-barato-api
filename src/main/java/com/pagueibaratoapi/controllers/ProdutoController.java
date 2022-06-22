package com.pagueibaratoapi.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pagueibaratoapi.models.Produto;

@RestController
@RequestMapping("/produto")
public class ProdutoController {
    
    @GetMapping
    public List<Produto> listar(){
        return true;
    }
}
