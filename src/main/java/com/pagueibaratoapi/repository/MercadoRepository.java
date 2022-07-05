package com.pagueibaratoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pagueibaratoapi.models.requests.Mercado;

public interface MercadoRepository extends JpaRepository<Mercado, Integer> {

    public boolean existsById(Integer id);

}