package com.pagueibaratoapi.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.pagueibaratoapi.models.requests.Estoque;
import com.pagueibaratoapi.models.requests.Mercado;
import com.pagueibaratoapi.models.requests.Produto;
import com.pagueibaratoapi.models.requests.Ramo;
import com.pagueibaratoapi.models.requests.Sugestao;

/**
 * Classe responsável por controlar as requisições de estoque.
 */
@RestController
@RequestMapping("/")
public class IndiceController {
    
    /**
     * Método responsável por listar todas as rotas.
     * @return - Lista com todas as rotas.
     */
    @GetMapping
    public List<Object> listar() {
        try {
            
            // Lista de objetos para adicionar links.
            List<Object> responseLinks = new ArrayList<Object>();
    
            // Adiciona link para listar categorias.
            responseLinks.add(
                linkTo(
                    methodOn(CategoriaController.class).listar()
                )
                .withRel("categoria")
            );
    
            // Adiciona link para listar estoques.
            responseLinks.add(
                linkTo(
                    methodOn(EstoqueController.class).listar(new Estoque())
                )
                .withRel("estoque")
            );
    
            // Adiciona link para listar mercados.
            responseLinks.add(
                linkTo(
                    methodOn(MercadoController.class).listar(new Mercado())
                )
                .withRel("mercado")
            );
    
            // Adiciona link para listar produtos.
            responseLinks.add(
                linkTo(
                    methodOn(ProdutoController.class).listar(new Produto())
                )
                .withRel("produto")
            );
    
            // Adiciona link para listar ramos.
            responseLinks.add(
                linkTo(
                    methodOn(RamoController.class).listar(new Ramo())
                )
                .withRel("ramo")
            );
    
            // Adiciona link para listar sugestões.
            responseLinks.add(
                linkTo(
                    methodOn(SugestaoController.class).listar(new Sugestao())
                )
                .withRel("sugestao")
            );
    
            // Adiciona link para listar usuários.
            responseLinks.add(
                linkTo(
                    methodOn(UsuarioController.class).listar()
                )
                .withRel("usuario")
            );
    
            // Retorna lista de links.
            return responseLinks;

        } catch (Exception e) {
            throw new ResponseStatusException(500, "erro_interno", e);
        }
    }
}
