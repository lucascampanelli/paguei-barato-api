package com.pagueibaratoapi.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
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

    public UsuarioController(UsuarioRepository usuarioRepository){
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping(produces = "application/json")
    public List<Usuario> listar(){
        return usuarioRepository.findAll();
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public Usuario criar(@RequestBody Usuario usuario){
        return usuarioRepository.save(usuario);
    }
}
