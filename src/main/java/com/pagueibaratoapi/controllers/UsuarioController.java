package com.pagueibaratoapi.controllers;

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
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.pagueibaratoapi.models.requests.Usuario;
import com.pagueibaratoapi.models.responses.ResponseUsuario;
import com.pagueibaratoapi.repository.UsuarioRepository;
import com.pagueibaratoapi.utils.EditaRecurso;
import com.pagueibaratoapi.utils.Senha;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {
    
    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository){
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping
    public ResponseUsuario criar(@RequestBody Usuario requestUsuario){
        if(requestUsuario.getEmail().isEmpty())
            throw new IllegalArgumentException("O preenchimento do e-mail é obrigatório");

        if(usuarioRepository.findByEmail(requestUsuario.getEmail()) != null)
            throw new IllegalArgumentException("O e-mail já está sendo utilizado");

        requestUsuario.setSenha(Senha.encriptar(requestUsuario.getSenha()));

        ResponseUsuario responseUsuario = new ResponseUsuario(usuarioRepository.save(requestUsuario));

        responseUsuario.add(
            linkTo(
                methodOn(UsuarioController.class).ler(responseUsuario.getId())
            ).withSelfRel()
        );

        return responseUsuario;
    }

    @GetMapping("/{id}")
    public ResponseUsuario ler(@PathVariable(value = "id") Integer id){
        ResponseUsuario responseUsuario = new ResponseUsuario(usuarioRepository.findById(id).get());

        if(responseUsuario != null){
            responseUsuario.add(
                linkTo(
                    methodOn(UsuarioController.class).listar()
                )
                .withRel("collection")
            );
        }

        return responseUsuario;
    }

    @GetMapping
    public List<ResponseUsuario> listar(){
        List<Usuario> usuarios = usuarioRepository.findAll();

        List<ResponseUsuario> responseUsuario = new ArrayList<ResponseUsuario>();

        for(Usuario usuario : usuarios){
            responseUsuario.add(new ResponseUsuario(usuario));
        }

        if(!responseUsuario.isEmpty()){
            for(ResponseUsuario usuario : responseUsuario){
                usuario.add(
                    linkTo(
                        methodOn(UsuarioController.class).ler(usuario.getId())
                    )
                    .withSelfRel()
                );
            }
        }

        return responseUsuario;
    }

    @PatchMapping("/{id}")
    public ResponseUsuario editar(@PathVariable(value = "id") Integer id, @RequestBody Usuario requestUsuario){
        Usuario usuarioAtual = usuarioRepository.findById(id).get();

        if(requestUsuario.getEmail() != null){
            if(requestUsuario.getEmail().isEmpty())
                throw new IllegalArgumentException("O e-mail informado é inválido");
        }
        
        ResponseUsuario responseUsuario = new ResponseUsuario(usuarioRepository.save(EditaRecurso.editarUsuario(usuarioAtual, requestUsuario)));

        responseUsuario.add(
            linkTo(
                methodOn(UsuarioController.class).ler(responseUsuario.getId())
            )
            .withSelfRel()
        );

        return responseUsuario;
    }

    @PutMapping("/{id}")
    public ResponseUsuario atualizar(@PathVariable(value = "id") Integer id, @RequestBody Usuario requestUsuario){
        if(requestUsuario.getEmail().isEmpty())
            throw new IllegalArgumentException("O preenchimento do e-mail é obrigatório");

        if(usuarioRepository.findByEmail(requestUsuario.getEmail()) != null)
            throw new IllegalArgumentException("O e-mail já está sendo utilizado");

        requestUsuario.setId(id);

        requestUsuario.setSenha(Senha.encriptar(requestUsuario.getSenha()));

        ResponseUsuario responseUsuario = new ResponseUsuario(usuarioRepository.save(requestUsuario));

        responseUsuario.add(
            linkTo(
                methodOn(UsuarioController.class).ler(responseUsuario.getId())
            )
            .withSelfRel()
        );

        return responseUsuario;
    }

    @DeleteMapping("/{id}")
    public Object remover(@PathVariable int id){
        Usuario usuarioDeletado = usuarioRepository.findById(id).get();

        usuarioDeletado.setId(id);
        usuarioDeletado.setNome("");
        usuarioDeletado.setEmail("");
        usuarioDeletado.setSenha("");
        usuarioDeletado.setLogradouro("");
        usuarioDeletado.setNumero(-1);
        usuarioDeletado.setComplemento(null);
        usuarioDeletado.setBairro("");
        usuarioDeletado.setCidade("");
        usuarioDeletado.setUf("--");
        usuarioDeletado.setCep("00000-000");

        usuarioRepository.save(usuarioDeletado);

        return linkTo(
                    methodOn(UsuarioController.class).listar()
                ).withRel("collection");
    }
}
