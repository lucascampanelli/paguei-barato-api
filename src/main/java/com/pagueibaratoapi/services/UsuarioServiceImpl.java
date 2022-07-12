package com.pagueibaratoapi.services;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.pagueibaratoapi.data.UsuarioService;
import com.pagueibaratoapi.models.requests.Usuario;
import com.pagueibaratoapi.repository.UsuarioRepository;

/**
 * Classe de serviço do usuário.
 */
@Component
public class UsuarioServiceImpl implements UserDetailsService {

    // Repositório do usuário.
    private final UsuarioRepository usuarioRepository;

    // Construtor.
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Busca o usuário pelo endereço de email.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Busca o usuário pelo endereço de email.
        Usuario usuario = usuarioRepository.findByEmail(username);

        // Se não encontrar, lança exceção.
        if(usuario == null)
            throw new UsernameNotFoundException("Usuário não encontrado");

        // Retorna o usuário.
        return new UsuarioService(Optional.of(usuario));
    }
}