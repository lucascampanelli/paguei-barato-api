package com.pagueibaratoapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pagueibaratoapi.models.requests.Sugestao;

public interface SugestaoRepository extends JpaRepository<Sugestao, Integer> {

    public List<Sugestao> findByEstoqueId(Integer estoqueId);

}