package com.pagueibaratoapi.data;

import java.util.Collection;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.pagueibaratoapi.models.requests.Usuario;

/**
 * Classe de serviços do usuário.
 */
@Component
public class UsuarioService implements UserDetails {

    // Modelo do usuário.
    private final Optional<Usuario> usuario;

    // Construtor.
    public UsuarioService(Optional<Usuario> usuario) {
        this.usuario = usuario;
    }

    // Obtém níveis de acesso.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    // Obtém senha.
    @Override
    public String getPassword() {
        // Retorna um modelo de usuário null ou sua senha
        return usuario.orElse(new Usuario()).getSenha();
    }

    // Obtém usuário.
    @Override
    public String getUsername() {
        // Retorna um modelo de usuário null ou seu email
        return usuario.orElse(new Usuario()).getEmail();
    }

    public int getId() {
        // Retorna um modelo de usuário null ou seu id
        return usuario.orElse(new Usuario()).getId();
    }

    // Obtém o objeto de usuário.
    public Usuario getUsuario() {
        return usuario.orElse(new Usuario());
    }

    // Verifica se o usuário está vencido.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Verifica se o usuário está bloqueado.
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // Verifica se a senha está expirada.
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Verifica se o usuário está ativo.
    @Override
    public boolean isEnabled() {
        return true;
    }
}
