package com.pagueibaratoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pagueibaratoapi.models.requests.Ramo;

public interface RamoRepository extends JpaRepository<Ramo, Integer> {
    
    public boolean existsById(Integer id);

    public boolean existsByNomeIgnoreCase(String nome);

}
