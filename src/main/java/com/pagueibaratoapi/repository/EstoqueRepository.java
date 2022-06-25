package com.pagueibaratoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pagueibaratoapi.models.Estoque;

public interface EstoqueRepository extends JpaRepository<Estoque, Integer> {

}
