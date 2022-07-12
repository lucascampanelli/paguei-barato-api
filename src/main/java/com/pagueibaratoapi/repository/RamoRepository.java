package com.pagueibaratoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pagueibaratoapi.models.requests.Ramo;

/**
 * Repositório de ramos.
 */
public interface RamoRepository extends JpaRepository<Ramo, Integer> {
    
    /**
     * Verifica se o ramo existe com base no id.
     * @param id - Id para verificar.
     * @return <b>true</b> se o id estiver cadastrado e <b>false</b> se não estiver.
     */
    public boolean existsById(Integer id);

    /**
     * Verifica se o ramo existe com base no nome ignorando maiúsculas e minúsculas.
     * @param nome - Nome do ramo para verificar.
     * @return <b>true</b> se o nome estiver cadastrado e <b>false</b> se não estiver.
     */
    public boolean existsByNomeIgnoreCase(String nome);

}
