package com.pagueibaratoapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pagueibaratoapi.models.requests.Estoque;

/*
 * Repositório de estoque.
 */
public interface EstoqueRepository extends JpaRepository<Estoque, Integer> {

    /**
     * Verifica se o estoque existe com base no id.
     * @param id - Id para verificar.
     * @return <b>true</b> se o id estiver cadastrado e <b>false</b> se não estiver.
     */
    public boolean existsById(Integer id);

    /**
     * Busca estoques que possuem o id de produto informado.
     * @param produtoId - Id do produto para buscar.
     * @return Lista de estoques.
     */
    public List<Estoque> findByProdutoId(Integer produtoId);

    /**
     * Busca estoques que possuem o id de mercado informado.
     * @param mercadoId - Id do mercado para buscar.
     * @return Lista de estoques.
     */
    public List<Estoque> findByMercadoId(Integer mercadoId);

    /**
     * Busca estoques que possuem o id de produto e o id de estoque informados.
     * @param produtoId - Id do produto para buscar.
     * @param mercadoId - Id do mercado para buscar.
     * @return Lista de estoques.
     */
    public Estoque findByProdutoIdAndMercadoId(Integer produtoId, Integer mercadoId);

}
