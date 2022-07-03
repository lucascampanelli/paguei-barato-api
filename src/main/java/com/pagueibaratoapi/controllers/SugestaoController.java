package com.pagueibaratoapi.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.pagueibaratoapi.models.responses.ResponsePagina;
import com.pagueibaratoapi.models.responses.ResponseSugestao;
import com.pagueibaratoapi.models.requests.Sugestao;
import com.pagueibaratoapi.repository.SugestaoRepository;
import com.pagueibaratoapi.utils.PaginaUtils;

@RestController
@RequestMapping("/sugestao")
public class SugestaoController {
    
    private final SugestaoRepository sugestaoRepository;

    public SugestaoController(SugestaoRepository sugestaoRepository) {
        this.sugestaoRepository = sugestaoRepository;
    }

    @PostMapping
    public ResponseSugestao criar(@RequestBody Sugestao requestSugestao) {
        requestSugestao.setPreco(requestSugestao.getPreco() * 100);

        ResponseSugestao responseSugestao = new ResponseSugestao(sugestaoRepository.save(requestSugestao));

        responseSugestao.add(
            linkTo(
                methodOn(SugestaoController.class).ler(responseSugestao.getId())
            )
            .withSelfRel()
        );

        responseSugestao.setPreco(responseSugestao.getPreco() / 100);

        return responseSugestao;
    }

    @GetMapping("/{id}")
    public ResponseSugestao ler(@PathVariable(value = "id") Integer id){
        ResponseSugestao responseSugestao = new ResponseSugestao(sugestaoRepository.findById(id).get());

        if(responseSugestao != null){
            responseSugestao.setPreco(responseSugestao.getPreco() / 100);
            responseSugestao.add(
                linkTo(
                    methodOn(SugestaoController.class).listar(new Sugestao())
                )
                .withRel("collection")
            );
        }

        return responseSugestao;
    }

    @GetMapping
    public List<ResponseSugestao> listar(Sugestao requestSugestao) {

        List<Sugestao> sugestoes = sugestaoRepository.findAll(
            Example.of(requestSugestao, ExampleMatcher
                                .matching()
                                .withIgnoreCase()
                                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)));

        List<ResponseSugestao> responseSugestao = new ArrayList<ResponseSugestao>();

        for(Sugestao sugestao : sugestoes){
            responseSugestao.add(new ResponseSugestao(sugestao));
        }

        if(!responseSugestao.isEmpty()){
            for(ResponseSugestao sugestao : responseSugestao) {
                sugestao.setPreco(sugestao.getPreco() / 100);
                sugestao.add(
                    linkTo(
                        methodOn(SugestaoController.class).ler(sugestao.getId())
                    )
                    .withSelfRel()
                );
            }
        }

        return responseSugestao;
    }

    @GetMapping(params = {"pagina", "limite"})
    public ResponsePagina listar(Sugestao requestSugestao, @RequestParam(required = false, defaultValue = "0") Integer pagina, @RequestParam(required = false, defaultValue = "10") Integer limite) {
        
        Page<Sugestao> paginaSugestao = sugestaoRepository.findAll(
            Example.of(requestSugestao, ExampleMatcher
                                .matching()
                                .withIgnoreCase()
                                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)), 
            PageRequest.of(pagina, limite));

        List<ResponseSugestao> sugestoes = new ArrayList<ResponseSugestao>();

        ResponsePagina responsePagina = PaginaUtils.criarResposta(pagina, limite, paginaSugestao, sugestoes);

        for(Sugestao sugestao : paginaSugestao.getContent()){
            sugestoes.add(new ResponseSugestao(sugestao));
        }

        responsePagina.add(
            linkTo(
                methodOn(SugestaoController.class).listar(requestSugestao, 0, limite)
            )
            .withRel("first")
        );

        if(!paginaSugestao.isEmpty()){
            if(pagina > 0){
                responsePagina.add(
                    linkTo(
                        methodOn(SugestaoController.class).listar(requestSugestao, pagina-1, limite)
                    )
                    .withRel("previous")
                );
            }
            if(pagina < paginaSugestao.getTotalPages()-1){
                responsePagina.add(
                    linkTo(
                        methodOn(SugestaoController.class).listar(requestSugestao, pagina+1, limite)
                    )
                    .withRel("next")
                );
            }
            responsePagina.add(
                linkTo(
                    methodOn(SugestaoController.class).listar(requestSugestao, paginaSugestao.getTotalPages()-1, limite)
                )
                .withRel("last")
            );

            for(ResponseSugestao sugestao : sugestoes){
                sugestao.setPreco(sugestao.getPreco() / 100);
                sugestao.add(
                    linkTo(
                        methodOn(SugestaoController.class).ler(sugestao.getId())
                    )
                    .withSelfRel()
                );
            }
        }

        return responsePagina;
    }

    @PatchMapping("/{id}")
    public ResponseSugestao editar(@PathVariable int id, @RequestBody Sugestao requestSugestao){
        Sugestao sugestaoAtual = sugestaoRepository.findById(id).get();

        if(requestSugestao.getPreco() != null)
            sugestaoAtual.setPreco(requestSugestao.getPreco());

        ResponseSugestao responseSugestao = new ResponseSugestao(sugestaoRepository.save(sugestaoAtual));

        responseSugestao.add(
            linkTo(
                methodOn(SugestaoController.class).ler(responseSugestao.getId())
            )
            .withSelfRel()
        );

        return responseSugestao;
    }

    @PutMapping("/{id}")
    public ResponseSugestao atualizar(@PathVariable int id, @RequestBody Sugestao requestSugestao){
        requestSugestao.setId(id);

        ResponseSugestao responseSugestao = new ResponseSugestao(sugestaoRepository.save(requestSugestao));

        responseSugestao.add(
            linkTo(
                methodOn(SugestaoController.class).ler(responseSugestao.getId())
            )
            .withSelfRel()
        );

        return responseSugestao;
    }

    @DeleteMapping("/{id}")
    public Object remover(@PathVariable int id){
        sugestaoRepository.deleteById(id);

        return linkTo(
                    methodOn(SugestaoController.class).listar(new Sugestao())
                ).withRel("collection");
    }
}
