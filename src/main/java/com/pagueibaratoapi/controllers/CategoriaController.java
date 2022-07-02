package com.pagueibaratoapi.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

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

import com.pagueibaratoapi.models.Categoria;
import com.pagueibaratoapi.repository.CategoriaRepository;

@RestController
@RequestMapping("/categoria")
public class CategoriaController {
    
    private final CategoriaRepository categoriaRepository;

    public CategoriaController(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @PostMapping
    public Categoria criar(@RequestBody Categoria requestCategoria) {
        Categoria responseCategoria = categoriaRepository.save(requestCategoria);;

        responseCategoria.add(
            linkTo(
                methodOn(CategoriaController.class).ler(responseCategoria.getId())
            )
            .withSelfRel()
        );

        return responseCategoria;
    }

    @GetMapping("/{id}")
    public Categoria ler(@PathVariable(value = "id") Integer id){
        Categoria responseCategoria = categoriaRepository.findById(id).get();

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
    public List<Categoria> listar() {
        List<Categoria> responseCategoria = categoriaRepository.findAll();

        if(!responseCategoria.isEmpty()) {
            for(Categoria categoria : responseCategoria) {
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
    public Categoria editar(@PathVariable int id, @RequestBody Categoria requestCategoria) {
        Categoria categoriaAtual = categoriaRepository.findById(id).get();
        
        if(requestCategoria.getNome() != null)
            categoriaAtual.setNome(requestCategoria.getNome());

        if(requestCategoria.getDescricao() != null)
            categoriaAtual.setDescricao(requestCategoria.getDescricao());

        Categoria responseCategoria = categoriaRepository.save(categoriaAtual);

        responseCategoria.add(
            linkTo(
                methodOn(CategoriaController.class).ler(responseCategoria.getId())
            )
            .withSelfRel()
        );

        return responseCategoria;
    }

    @PutMapping("/{id}")
    public Categoria atualizar(@PathVariable int id, @RequestBody Categoria requestCategoria){
        requestCategoria.setId(id);

        Categoria responseCategoria = categoriaRepository.save(requestCategoria);

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
