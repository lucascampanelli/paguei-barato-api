package com.pagueibaratoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pagueibaratoapi.models.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

}
