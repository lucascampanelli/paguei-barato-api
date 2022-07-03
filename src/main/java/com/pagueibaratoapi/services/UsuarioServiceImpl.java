package com.pagueibaratoapi.services;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.pagueibaratoapi.data.UsuarioService;
import com.pagueibaratoapi.models.requests.Usuario;
import com.pagueibaratoapi.repository.UsuarioRepository;

@Component
public class UsuarioServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(username);

        if(usuario == null)
            throw new UsernameNotFoundException("Usuário não encontrado");

        return new UsuarioService(Optional.of(usuario));
    }

}