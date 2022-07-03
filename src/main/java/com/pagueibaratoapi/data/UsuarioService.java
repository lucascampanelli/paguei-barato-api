package com.pagueibaratoapi.data;

import java.util.Collection;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.pagueibaratoapi.models.requests.Usuario;

@Component
public class UsuarioService implements UserDetails{

    private final Optional<Usuario> usuario;

    public UsuarioService(Optional<Usuario> usuario) {
        this.usuario = usuario;
    }
    
    // Autorizações do usuário
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        // Retorna um modelo de usuário null ou sua senha
        return usuario.orElse(new Usuario()).getSenha();
    }

    @Override
    public String getUsername() {
        // Retorna um modelo de usuário null ou seu email
        return usuario.orElse(new Usuario()).getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    
}
