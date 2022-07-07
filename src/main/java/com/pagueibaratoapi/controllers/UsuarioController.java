package com.pagueibaratoapi.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.pagueibaratoapi.models.exceptions.DadosConflitantesException;
import com.pagueibaratoapi.models.exceptions.DadosInvalidosException;
import com.pagueibaratoapi.models.requests.Usuario;
import com.pagueibaratoapi.models.responses.ResponseUsuario;
import com.pagueibaratoapi.repository.UsuarioRepository;
import com.pagueibaratoapi.utils.EditaRecurso;
import com.pagueibaratoapi.utils.Senha;
import com.pagueibaratoapi.utils.Tratamento;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {
    
    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository){
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping
    public ResponseUsuario criar(@RequestBody Usuario requestUsuario){
        try {

            Tratamento.validarUsuario(requestUsuario, false);

            if(usuarioRepository.findByEmail(requestUsuario.getEmail()) != null)
                throw new DadosConflitantesException("email_em_uso");
    
            requestUsuario.setSenha(Senha.encriptar(requestUsuario.getSenha()));
    
            ResponseUsuario responseUsuario = new ResponseUsuario(usuarioRepository.save(requestUsuario));
    
            responseUsuario.add(
                linkTo(
                    methodOn(UsuarioController.class).ler(responseUsuario.getId())
                ).withSelfRel()
            );
    
            return responseUsuario;

        } catch (DadosConflitantesException e) {
            throw new ResponseStatusException(409, e.getMessage(), e);
        } catch (DadosInvalidosException e) {
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(500, "erro_insercao", e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    @GetMapping("/{id}")
    public ResponseUsuario ler(@PathVariable(value = "id") Integer id){
        try {

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

        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    @GetMapping
    public List<ResponseUsuario> listar(){
        try {

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

        } catch(NullPointerException e) {
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch(UnsupportedOperationException e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch(Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    @PatchMapping("/{id}")
    public ResponseUsuario editar(@PathVariable(value = "id") Integer id, @RequestBody Usuario requestUsuario){
        try {

            Tratamento.validarUsuario(requestUsuario, true);

            if(usuarioRepository.findByEmail(requestUsuario.getEmail()) != null)
                throw new DadosConflitantesException("email_em_uso");

            Usuario usuarioAtual = usuarioRepository.findById(id).get();
            
            ResponseUsuario responseUsuario = new ResponseUsuario(usuarioRepository.save(EditaRecurso.editarUsuario(usuarioAtual, requestUsuario)));
    
            responseUsuario.add(
                linkTo(
                    methodOn(UsuarioController.class).ler(responseUsuario.getId())
                )
                .withSelfRel()
            );
    
            return responseUsuario;

        } catch (DadosConflitantesException e) {
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (DadosInvalidosException e) {
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(500, "erro_insercao", e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    @PutMapping("/{id}")
    public ResponseUsuario atualizar(@PathVariable(value = "id") Integer id, @RequestBody Usuario requestUsuario){
        try {

            Tratamento.validarUsuario(requestUsuario, false);

            if(usuarioRepository.findByEmail(requestUsuario.getEmail()) != null)
                throw new DadosConflitantesException("email_em_uso");
    
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

        } catch (DadosConflitantesException e) {
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (DadosInvalidosException e) {
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(404, e.getMessage(), e);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(500, "erro_insercao", e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    @DeleteMapping("/{id}")
    public Object remover(@PathVariable int id){
        try {

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

        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(404, e.getMessage(), e);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(500, "erro_remocao", e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }
}
