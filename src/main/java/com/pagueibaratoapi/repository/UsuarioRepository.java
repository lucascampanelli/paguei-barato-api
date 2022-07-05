package com.pagueibaratoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.pagueibaratoapi.models.requests.Usuario;

@Component
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    public boolean existsById(Integer id);

    public Usuario findByEmail(String email);

}
