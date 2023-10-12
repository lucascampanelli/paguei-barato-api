package com.pagueibaratoapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pagueibaratoapi.models.requests.Produto;

/**
 * Repositório de produtos.
 */
public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
    
    /**
     * Verifica se o produto existe com base no id.
     * @param id - Id para verificar.
     * @return <b>true</b> se o id estiver cadastrado e <b>false</b> se não estiver.
     */
    public boolean existsById(Integer id);

    /**
     * Busca produtos que possuem as mesmas características do produto informado.
     * @param nome - Nome do produto para buscar.
     * @param marca - Marca do produto para buscar.
     * @param tamanho - Tamanho do produto para buscar.
     * @param cor - Cor opcional do produto para buscar.
     * @return Produto encontrado ou null.
     */
    @Query("SELECT p FROM Produto p WHERE UPPER(p.nome) = UPPER(:nome) AND UPPER(p.marca) = UPPER(:marca) AND UPPER(p.tamanho) LIKE UPPER(CONCAT('%',:tamanho,'%')) AND (p.cor IS NULL OR UPPER(p.cor) = UPPER(:cor))")
    public Produto findByCaracteristicas(
        @Param("nome") String nome, 
        @Param("marca") String marca, 
        @Param("tamanho") String tamanho, 
        @Param("cor") String cor
    );

    /**
     * Busca os produtos onde um dos seus campos correspondem ao texto de pesquisa, incluindo a relação do nome da categoria.
     * @param pesquisa - Texto para pesquisa.
     * @return Lista de produtos encontrados.
     */
    @Query("SELECT p FROM Produto p WHERE UPPER(p.nome) LIKE UPPER(CONCAT('%',:pesquisa,'%')) OR UPPER(p.marca) LIKE UPPER(CONCAT('%',:pesquisa,'%')) OR UPPER(p.tamanho) LIKE UPPER(CONCAT('%',:pesquisa,'%')) OR UPPER(p.cor) LIKE UPPER(CONCAT('%',:pesquisa,'%')) OR UPPER(p.categoria.nome) LIKE UPPER(CONCAT('%',:pesquisa,'%'))")
    public List<Produto> findByCorrespondenciasPesquisa(
        @Param("pesquisa") String pesquisa
    );
}