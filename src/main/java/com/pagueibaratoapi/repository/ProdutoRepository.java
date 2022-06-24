package com.pagueibaratoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pagueibaratoapi.models.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
    
}