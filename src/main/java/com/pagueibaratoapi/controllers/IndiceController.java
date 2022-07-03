package com.pagueibaratoapi.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pagueibaratoapi.models.requests.Estoque;
import com.pagueibaratoapi.models.requests.Mercado;
import com.pagueibaratoapi.models.requests.Produto;
import com.pagueibaratoapi.models.requests.Ramo;
import com.pagueibaratoapi.models.requests.Sugestao;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/")
public class IndiceController {
    
    @GetMapping
    public List<Object> listar(){
        List<Object> responseLinks = new ArrayList<Object>();

        responseLinks.add(
            linkTo(
                methodOn(CategoriaController.class).listar()
            )
            .withRel("categoria")
        );

        responseLinks.add(
            linkTo(
                methodOn(EstoqueController.class).listar(new Estoque())
            )
            .withRel("estoque")
        );

        responseLinks.add(
            linkTo(
                methodOn(MercadoController.class).listar(new Mercado())
            )
            .withRel("mercado")
        );

        responseLinks.add(
            linkTo(
                methodOn(ProdutoController.class).listar(new Produto())
            )
            .withRel("produto")
        );

        responseLinks.add(
            linkTo(
                methodOn(RamoController.class).listar(new Ramo())
            )
            .withRel("ramo")
        );

        responseLinks.add(
            linkTo(
                methodOn(SugestaoController.class).listar(new Sugestao())
            )
            .withRel("sugestao")
        );

        responseLinks.add(
            linkTo(
                methodOn(UsuarioController.class).listar()
            )
            .withRel("usuario")
        );

        return responseLinks;
    }
}
