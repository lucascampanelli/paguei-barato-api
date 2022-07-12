package com.pagueibaratoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.pagueibaratoapi.models.requests.Usuario;

/**
 * Repositório de usuários.
 */
@Component
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    /**
     * Verifica se o usuário existe com base no id.
     * @param id - Id para verificar.
     * @return <b>true</b> se o id estiver cadastrado e <b>false</b> se não estiver.
     */
    public boolean existsById(Integer id);

    /**
     * Busca um usuário pelo endereço de email.
     * @param email - Email do usuário para buscar.
     * @return Usuário encontrado ou null.
     */
    public Usuario findByEmail(String email);

}
