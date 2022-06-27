package com.pagueibaratoapi.controllers;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pagueibaratoapi.models.Usuario;
import com.pagueibaratoapi.repository.UsuarioRepository;
import com.pagueibaratoapi.utils.EditaRecurso;
import com.pagueibaratoapi.utils.Senha;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {
    
    private final UsuarioRepository usuarioRepository;
    private BCryptPasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioRepository usuarioRepository){
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping
    public Usuario criar(@RequestBody Usuario requestUsuario){
        if(requestUsuario.getEmail().isEmpty())
            throw new IllegalArgumentException("O preenchimento do e-mail é obrigatório");

        if(usuarioRepository.findByEmail(requestUsuario.getEmail()) != null)
            throw new IllegalArgumentException("O e-mail já está sendo utilizado");

        passwordEncoder = new BCryptPasswordEncoder();

        requestUsuario.setSenha(passwordEncoder.encode(Senha.salgarSenha(requestUsuario.getSenha())));

        return usuarioRepository.save(requestUsuario);
    }

    @GetMapping("/{id}")
    public Usuario ler(@PathVariable(value = "id") Integer id){
        return usuarioRepository.findById(id).get();
    }

    @GetMapping
    public List<Usuario> listar(){
        return usuarioRepository.findAll();
    }

    @PatchMapping("/{id}")
    public Usuario editar(@PathVariable(value = "id") Integer id, @RequestBody Usuario requestUsuario){
        Usuario usuarioAtual = usuarioRepository.findById(id).get();

        return usuarioRepository.save(EditaRecurso.editarUsuario(usuarioAtual, requestUsuario));
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable int id){
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
    }
}
