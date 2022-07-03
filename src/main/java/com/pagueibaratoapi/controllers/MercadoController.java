package com.pagueibaratoapi.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

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

import com.pagueibaratoapi.models.requests.Mercado;
import com.pagueibaratoapi.models.responses.ResponseMercado;
import com.pagueibaratoapi.models.responses.ResponsePagina;
import com.pagueibaratoapi.repository.MercadoRepository;
import com.pagueibaratoapi.utils.PaginaUtils;

@RestController
@RequestMapping("/mercado")
public class MercadoController {
    
    private final MercadoRepository mercadoRepository;

    public MercadoController(MercadoRepository mercadoRepository) {
        this.mercadoRepository = mercadoRepository;
    }

    @PostMapping
    public ResponseMercado criar(@RequestBody Mercado requestMercado){
        ResponseMercado responseMercado = new ResponseMercado(mercadoRepository.save(requestMercado));

        responseMercado.add(
            linkTo(
                methodOn(MercadoController.class).ler(responseMercado.getId())
            )
            .withSelfRel()
        );

        return responseMercado;
    }

    @GetMapping("/{id}")
    public ResponseMercado ler(@PathVariable(value = "id") Integer id){
        ResponseMercado responseMercado = new ResponseMercado(mercadoRepository.findById(id).get());

        if(responseMercado != null){
            responseMercado.add(
                linkTo(
                    methodOn(MercadoController.class).listar(new Mercado())
                )
                .withRel("collection")
            );
        }

        return responseMercado;
    }

    @GetMapping
    public List<ResponseMercado> listar(Mercado requestMercado){
        List<Mercado> mercados = mercadoRepository.findAll(
                                            Example.of(requestMercado, ExampleMatcher
                                                                .matching()
                                                                .withIgnoreCase()
                                                                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)));
        
        List<ResponseMercado> responseMercado = new ArrayList<ResponseMercado>();

        for(Mercado mercado : mercados){
            responseMercado.add(new ResponseMercado(mercado));
        }

        if(!responseMercado.isEmpty()){
            for(ResponseMercado mercado : responseMercado){
                mercado.add(
                    linkTo(
                        methodOn(MercadoController.class).ler(mercado.getId())
                    )
                    .withSelfRel()
                );
            }
        }
        
        return responseMercado;
    }

    @GetMapping(params = {"pagina", "limite"})
    public ResponsePagina listar(Mercado requestMercado, @RequestParam(required = false, defaultValue = "0") Integer pagina, @RequestParam(required = false, defaultValue = "10") Integer limite){

        Page<Mercado> paginaMercado = mercadoRepository.findAll(
                                                            Example.of(requestMercado, ExampleMatcher
                                                                                .matching()
                                                                                .withIgnoreCase()
                                                                                .withStringMatcher(
                                                                                    ExampleMatcher.StringMatcher.CONTAINING)), 
                                                                                    PageRequest.of(pagina, limite));

        
        List<ResponseMercado> mercados = new ArrayList<ResponseMercado>();

        for(Mercado mercado : paginaMercado.getContent()){
            mercados.add(new ResponseMercado(mercado));
        }

        ResponsePagina responseMercado = PaginaUtils.criarResposta(pagina, limite, paginaMercado);

        responseMercado.add(
            linkTo(
                methodOn(MercadoController.class).listar(requestMercado, 0, limite)
            )
            .withRel("first")
        );

        if(!paginaMercado.isEmpty()){
            if(pagina > 0){
                responseMercado.add(
                    linkTo(
                        methodOn(MercadoController.class).listar(requestMercado, pagina-1, limite)
                    )
                    .withRel("previous")
                );
            }
            if(pagina < paginaMercado.getTotalPages()-1){
                responseMercado.add(
                    linkTo(
                        methodOn(MercadoController.class).listar(requestMercado, pagina+1, limite)
                    )
                    .withRel("next")
                );
            }
            responseMercado.add(
                linkTo(
                    methodOn(MercadoController.class).listar(requestMercado, paginaMercado.getTotalPages()-1, limite)
                )
                .withRel("last")
            );

            for(ResponseMercado mercado : mercados){
                mercado.add(
                    linkTo(
                        methodOn(MercadoController.class).ler(mercado.getId())
                    )
                    .withSelfRel()
                );
            }
        }
        
        return responseMercado;
    }

    @PatchMapping("/{id}")
    public ResponseMercado editar(@PathVariable int id, @RequestBody Mercado requestMercado){
        Mercado mercadoAtual = mercadoRepository.findById(id).get();
        
        if(requestMercado.getRamoId() != null)
            mercadoAtual.setRamoId(requestMercado.getRamoId());

        if(requestMercado.getNome() != null)
            mercadoAtual.setNome(requestMercado.getNome());

        if(requestMercado.getLogradouro() != null)
            mercadoAtual.setLogradouro(requestMercado.getLogradouro());

        if(requestMercado.getNumero() != null)
            mercadoAtual.setNumero(requestMercado.getNumero());

        if(requestMercado.getComplemento() != null){
            if(requestMercado.getComplemento().trim().isEmpty())
                mercadoAtual.setComplemento(null);
            else
                mercadoAtual.setComplemento(requestMercado.getComplemento());
        }

        if(requestMercado.getBairro() != null)
            mercadoAtual.setBairro(requestMercado.getBairro());

        if(requestMercado.getCidade() != null)
            mercadoAtual.setCidade(requestMercado.getCidade());
            
        if(requestMercado.getUf() != null)
            mercadoAtual.setUf(requestMercado.getUf());
        
        if(requestMercado.getCep() != null)
            mercadoAtual.setCep(requestMercado.getCep());

        ResponseMercado responseMercado = new ResponseMercado(mercadoRepository.save(mercadoAtual));
        
        responseMercado.add(
            linkTo(
                methodOn(MercadoController.class).ler(responseMercado.getId())
            )
            .withSelfRel()
        );

        return responseMercado;
    }

    @PutMapping("/{id}")
    public ResponseMercado atualizar(@PathVariable int id, @RequestBody Mercado requestMercado){
        requestMercado.setId(id);

        ResponseMercado responseMercado = new ResponseMercado(mercadoRepository.save(requestMercado));
        
        responseMercado.add(
            linkTo(
                methodOn(MercadoController.class).ler(responseMercado.getId())
            )
            .withSelfRel()
        );

        return responseMercado;
    }

    @DeleteMapping("/{id}")
    public Object remover(@PathVariable int id){
        mercadoRepository.deleteById(id);

        return linkTo(
                            methodOn(MercadoController.class).listar(new Mercado())
                        )
                        .withRel("collection");
    }
}
