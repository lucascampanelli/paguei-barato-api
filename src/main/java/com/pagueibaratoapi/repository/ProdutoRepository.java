package com.pagueibaratoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pagueibaratoapi.models.requests.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
    
    public boolean existsById(Integer id);

    @Query("SELECT p FROM Produto p WHERE UPPER(p.nome) = UPPER(:nome) AND UPPER(p.marca) = UPPER(:marca) AND UPPER(p.tamanho) LIKE UPPER(CONCAT('%',:tamanho,'%')) AND (p.cor IS NULL OR UPPER(p.cor) = UPPER(:cor))")
    public Produto findByCaracteristicas(
        @Param("nome") String nome, 
        @Param("marca") String marca, 
        @Param("tamanho") String tamanho, 
        @Param("cor") String cor
    );
}