package com.pagueibaratoapi.controllers;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.pagueibaratoapi.models.Estoque;
import com.pagueibaratoapi.models.ResponsePagina;
import com.pagueibaratoapi.repository.EstoqueRepository;
import com.pagueibaratoapi.utils.PaginaUtils;

@RestController
@RequestMapping("/estoque")
public class EstoqueController {
    
    private final EstoqueRepository estoqueRepository;

    public EstoqueController(EstoqueRepository estoqueRepository) {
        this.estoqueRepository = estoqueRepository;
    }

    @PostMapping
    public Estoque criar(@RequestBody Estoque requestEstoque) {
        return estoqueRepository.save(requestEstoque);
    }

    @GetMapping("/{id}")
    public Estoque ler(@PathVariable(value = "id") Integer id){
        Estoque responseEstoque = estoqueRepository.findById(id).get();

        if(responseEstoque != null){
            responseEstoque.add(
                linkTo(
                    methodOn(EstoqueController.class).listar(new Estoque())
                )
                .withRel("collection")
            );
        }

        return responseEstoque;
    }

    @GetMapping
    public List<Estoque> listar(Estoque requestEstoque){

        List<Estoque> responseEstoque = estoqueRepository.findAll(
            Example.of(requestEstoque, ExampleMatcher
                                .matching()
                                .withIgnoreCase()
                                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)));

        if(!responseEstoque.isEmpty()){
            for(Estoque estoque : responseEstoque){
                estoque.add(
                    linkTo(
                        methodOn(EstoqueController.class).ler(estoque.getId())
                    )
                    .withSelfRel()
                );
            }
        }

        return responseEstoque;
    }

    @GetMapping(params = {"pagina", "limite"})
    public ResponsePagina listar(Estoque requestEstoque, @RequestParam(required = false, defaultValue = "0") Integer pagina, @RequestParam(required = false, defaultValue = "10") Integer limite){

        Page<Estoque> paginaEstoque = estoqueRepository.findAll(
            Example.of(requestEstoque, ExampleMatcher
                                .matching()
                                .withIgnoreCase()
                                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)),
            PageRequest.of(pagina, limite));

        ResponsePagina responseEstoque = PaginaUtils.criarResposta(pagina, limite, paginaEstoque);
        
        responseEstoque.add(
            linkTo(
                methodOn(EstoqueController.class).listar(requestEstoque, 0, limite)
            )
            .withRel("first")
        );

        if(!paginaEstoque.isEmpty()){
            if(pagina > 0){
                responseEstoque.add(
                    linkTo(
                        methodOn(EstoqueController.class).listar(requestEstoque, pagina-1, limite)
                    )
                    .withRel("previous")
                );
            }
            if(pagina < paginaEstoque.getTotalPages()-1){
                responseEstoque.add(
                    linkTo(
                        methodOn(EstoqueController.class).listar(requestEstoque, pagina+1, limite)
                    )
                    .withRel("next")
                );
            }
            responseEstoque.add(
                linkTo(
                    methodOn(EstoqueController.class).listar(requestEstoque, paginaEstoque.getTotalPages()-1, limite)
                )
                .withRel("last")
            );

            for(Estoque estoque : paginaEstoque){
                estoque.add(
                    linkTo(
                        methodOn(EstoqueController.class).ler(estoque.getId())
                    )
                    .withSelfRel()
                );
            }
        }

        return responseEstoque;
        
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable int id){
        estoqueRepository.deleteById(id);
    }
    
}
