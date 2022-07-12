package com.pagueibaratoapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pagueibaratoapi.models.requests.Sugestao;

/*
 * Repositório de sugestões.
 */
public interface SugestaoRepository extends JpaRepository<Sugestao, Integer> {

    /**
     * Busca as sugestões que possuem o id de estoque informado.
     * @param estoqueId - Id do estoque para buscar.
     * @return Lista de sugestões.
     */
    public List<Sugestao> findByEstoqueId(Integer estoqueId);

}