package com.pagueibaratoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pagueibaratoapi.models.requests.Categoria;

/**
 * Repositório de categorias.
 */
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    
    /**
     * Verifica se a categoria existe com base no id.
     * @param id - Id para verificar.
     * @return <b>true</b> se o id estiver cadastrado e <b>false</b> se não estiver.
     */
    public boolean existsById(Integer id);

    /**
     * Verifica se a categoria existe com base no nome ignorando maiúsculas e minúsculas.
     * @param nome - Nome da categoria para verificar.
     * @return <b>true</b> se o nome estiver cadastrado e <b>false</b> se não estiver.
     */
    public boolean existsByNomeIgnoreCase(String nome);

}