package com.pagueibaratoapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pagueibaratoapi.models.requests.Estoque;

public interface EstoqueRepository extends JpaRepository<Estoque, Integer> {

    public boolean existsById(Integer id);

    public List<Estoque> findByProdutoId(Integer produtoId);

}
