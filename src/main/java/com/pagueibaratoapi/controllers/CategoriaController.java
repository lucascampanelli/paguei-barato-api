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
        return categoriaRepository.save(requestCategoria);
    }

    @GetMapping("/{id}")
    public Categoria ler(@PathVariable(value = "id") Integer id){
        return categoriaRepository.findById(id).get();
    }

    @GetMapping
    public List<Categoria> listar() {
        List<Categoria> responseCategoria = categoriaRepository.findAll();

        if(!responseCategoria.isEmpty()) {
            for(Categoria categoria : responseCategoria) {
                categoria.add(linkTo(methodOn(CategoriaController.class).ler(categoria.getId())).withSelfRel());
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

        return categoriaRepository.save(categoriaAtual);
    }

    @PutMapping("/{id}")
    public void atualizar(@PathVariable int id, @RequestBody Categoria requestCategoria){
        requestCategoria.setId(id);
        categoriaRepository.save(requestCategoria);
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable int id){
        categoriaRepository.deleteById(id);
    }
}
