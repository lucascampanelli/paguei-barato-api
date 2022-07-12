package com.pagueibaratoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pagueibaratoapi.models.requests.Mercado;

/**
 * Repositório de mercados.
 */
public interface MercadoRepository extends JpaRepository<Mercado, Integer> {

    /**
     * Verifica se o mercado existe com base no id.
     * @param id - Id para verificar.
     * @return <b>true</b> se o id estiver cadastrado e <b>false</b> se não estiver.
     */
    public boolean existsById(Integer id);

    /**
     * Verifica se o mercado existe com base no nome ignorando maiúsculas e minúsculas.
     * @param nome - Nome do mercado para verificar.
     * @return <b>true</b> se o nome estiver cadastrado e <b>false</b> se não estiver.
     */
    public boolean existsByNomeIgnoreCase(String nome);
    
    /**
     * Busca um mercado que possui o endereço informado.
     * @param logradouro - Logradouro do mercado para buscar.
     * @param numero - Número em Integer do mercado para buscar.
     * @param complemento - Complemento opcional do mercado para buscar.
     * @param bairro - Bairro do mercado para buscar.
     * @param cidade - Cidade do mercado para buscar.
     * @param uf - UF do mercado dentre os UFs brasileiros válidos para buscar.
     * @param cep - CEP de 8 dígitos com hífen do mercado para buscar.
     * @return Mercado encontrado ou null.
     */
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