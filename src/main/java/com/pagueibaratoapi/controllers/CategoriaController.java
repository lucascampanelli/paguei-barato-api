package com.pagueibaratoapi.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pagueibaratoapi.models.requests.Categoria;
import com.pagueibaratoapi.models.responses.ResponseCategoria;
import com.pagueibaratoapi.repository.CategoriaRepository;

@RestController
@RequestMapping("/categoria")
public class CategoriaController {
    
    private final CategoriaRepository categoriaRepository;

    public CategoriaController(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @PostMapping
    public ResponseCategoria criar(@RequestBody Categoria requestCategoria) {
        ResponseCategoria responseCategoria = new ResponseCategoria(categoriaRepository.save(requestCategoria));

        responseCategoria.add(
            linkTo(
                methodOn(CategoriaController.class).ler(responseCategoria.getId())
            )
            .withSelfRel()
        );

        return responseCategoria;
    }

    @GetMapping("/{id}")
    public ResponseCategoria ler(@PathVariable(value = "id") Integer id){
        ResponseCategoria responseCategoria = new ResponseCategoria(categoriaRepository.findById(id).get());

        if(responseCategoria != null){
            responseCategoria.add(
                linkTo(
                    methodOn(CategoriaController.class).listar()
                )
                .withRel("collection")
            );
        }

        return responseCategoria;
    }

    @GetMapping
    public List<ResponseCategoria> listar() {
        List<Categoria> categorias = categoriaRepository.findAll();
        List<ResponseCategoria> responseCategoria = new ArrayList<ResponseCategoria>();

        for(Categoria categoria : categorias){
            responseCategoria.add(
                new ResponseCategoria(categoria)
            );
        }

        if(!responseCategoria.isEmpty()) {
            for(ResponseCategoria categoria : responseCategoria) {
                categoria.add(
                    linkTo(
                        methodOn(CategoriaController.class).ler(categoria.getId())
                    )
                    .withSelfRel()
                );
            }
        }

        return responseCategoria;
    }

    @PatchMapping("/{id}")
    public ResponseCategoria editar(@PathVariable int id, @RequestBody Categoria requestCategoria) {
        Categoria categoriaAtual = categoriaRepository.findById(id).get();
        
        if(requestCategoria.getNome() != null)
            categoriaAtual.setNome(requestCategoria.getNome());

        if(requestCategoria.getDescricao() != null)
            categoriaAtual.setDescricao(requestCategoria.getDescricao());

        ResponseCategoria responseCategoria = new ResponseCategoria(categoriaRepository.save(categoriaAtual));

        responseCategoria.add(
            linkTo(
                methodOn(CategoriaController.class).ler(responseCategoria.getId())
            )
            .withSelfRel()
        );

        return responseCategoria;
    }

    @PutMapping("/{id}")
    public ResponseCategoria atualizar(@PathVariable int id, @RequestBody Categoria requestCategoria){
        requestCategoria.setId(id);

        ResponseCategoria responseCategoria = new ResponseCategoria(categoriaRepository.save(requestCategoria));

        responseCategoria.add(
            linkTo(
                methodOn(CategoriaController.class).ler(responseCategoria.getId())
            )
            .withSelfRel()
        );

        return responseCategoria;
    }

    @DeleteMapping("/{id}")
    public Object remover(@PathVariable int id){
        categoriaRepository.deleteById(id);

        return linkTo(
                    methodOn(CategoriaController.class).listar()
                )
                .withRel("collection");
    }
}
