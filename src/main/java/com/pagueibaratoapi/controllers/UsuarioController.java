package com.pagueibaratoapi.controllers;

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

import com.pagueibaratoapi.models.Usuario;
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
    public Usuario criar(@RequestBody Usuario requestUsuario){
        if(requestUsuario.getEmail().isEmpty())
            throw new IllegalArgumentException("O preenchimento do e-mail é obrigatório");

        if(usuarioRepository.findByEmail(requestUsuario.getEmail()) != null)
            throw new IllegalArgumentException("O e-mail já está sendo utilizado");

        requestUsuario.setSenha(Senha.encriptar(requestUsuario.getSenha()));

        Usuario responseUsuario = usuarioRepository.save(requestUsuario);

        responseUsuario.add(
            linkTo(
                methodOn(UsuarioController.class).ler(responseUsuario.getId())
            ).withSelfRel()
        );

        return responseUsuario;
    }

    @GetMapping("/{id}")
    public Usuario ler(@PathVariable(value = "id") Integer id){
        Usuario responseUsuario = usuarioRepository.findById(id).get();

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
    public List<Usuario> listar(){
        List<Usuario> responseUsuario = usuarioRepository.findAll();

        if(!responseUsuario.isEmpty()){
            for(Usuario usuario : responseUsuario){
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
    public Usuario editar(@PathVariable(value = "id") Integer id, @RequestBody Usuario requestUsuario){
        Usuario usuarioAtual = usuarioRepository.findById(id).get();

        if(requestUsuario.getEmail() != null){
            if(requestUsuario.getEmail().isEmpty())
                throw new IllegalArgumentException("O e-mail informado é inválido");
        }
        
        Usuario responseUsuario = usuarioRepository.save(EditaRecurso.editarUsuario(usuarioAtual, requestUsuario));

        responseUsuario.add(
            linkTo(
                methodOn(UsuarioController.class).ler(responseUsuario.getId())
            )
            .withSelfRel()
        );

        return responseUsuario;
    }

    @PutMapping("/{id}")
    public Usuario atualizar(@PathVariable(value = "id") Integer id, @RequestBody Usuario requestUsuario){
        if(requestUsuario.getEmail().isEmpty())
            throw new IllegalArgumentException("O preenchimento do e-mail é obrigatório");

        if(usuarioRepository.findByEmail(requestUsuario.getEmail()) != null)
            throw new IllegalArgumentException("O e-mail já está sendo utilizado");

        requestUsuario.setId(id);

        requestUsuario.setSenha(Senha.encriptar(requestUsuario.getSenha()));

        Usuario responseUsuario = usuarioRepository.save(requestUsuario);

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
