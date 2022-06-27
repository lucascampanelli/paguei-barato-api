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
        requestUsuario.setSenha(passwordEncoder.encode(requestUsuario.getSenha()));
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

        if(requestUsuario.getNome() != null)
            usuarioAtual.setNome(requestUsuario.getNome());
        
        if(requestUsuario.getEmail() != null)
            usuarioAtual.setEmail(requestUsuario.getEmail());
        
        if(requestUsuario.getSenha() != null)
            usuarioAtual.setSenha(requestUsuario.getSenha());

        if(requestUsuario.getLogradouro() != null)
            usuarioAtual.setLogradouro(requestUsuario.getLogradouro());

        if(requestUsuario.getNumero() != null)
            usuarioAtual.setNumero(requestUsuario.getNumero());

        if(requestUsuario.getComplemento() != null){
            if(requestUsuario.getComplemento() == "")
                usuarioAtual.setComplemento(null);
            else
                usuarioAtual.setComplemento(requestUsuario.getComplemento());
        }

        if(requestUsuario.getBairro() != null)
            usuarioAtual.setBairro(requestUsuario.getBairro());

        if(requestUsuario.getCidade() != null)
            usuarioAtual.setCidade(requestUsuario.getCidade());

        if(requestUsuario.getUf() != null)
            usuarioAtual.setUf(requestUsuario.getUf());
        
        if(requestUsuario.getCep() != null)
            usuarioAtual.setCep(requestUsuario.getCep());

        return usuarioRepository.save(usuarioAtual);
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
