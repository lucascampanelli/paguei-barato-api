package com.pagueibaratoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pagueibaratoapi.models.requests.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    public boolean existsById(Integer id);

    public boolean existsByNome(String nome);

}