package com.pagueibaratoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pagueibaratoapi.models.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

}