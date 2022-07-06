package com.pagueibaratoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pagueibaratoapi.models.requests.Mercado;

public interface MercadoRepository extends JpaRepository<Mercado, Integer> {

    public boolean existsById(Integer id);

    public boolean existsByNomeIgnoreCase(String nome);
    
    @Query("SELECT m FROM Mercado m WHERE UPPER(m.logradouro) LIKE UPPER(CONCAT('%',:logradouro,'%')) AND m.numero = :numero AND (m.complemento IS NULL OR UPPER(m.complemento) LIKE UPPER(CONCAT('%',:complemento,'%'))) AND UPPER(m.bairro) = UPPER(:bairro) AND UPPER(m.cidade) = UPPER(:cidade) AND UPPER(m.uf) = UPPER(:uf) AND m.cep = :cep")
    public Mercado findByEndereco(
        @Param("logradouro") String logradouro, 
        @Param("numero") Integer numero, 
        @Param("complemento") String complemento, 
        @Param("bairro") String bairro, 
        @Param("cidade") String cidade, 
        @Param("uf") String uf, 
        @Param("cep") String cep
    );

}