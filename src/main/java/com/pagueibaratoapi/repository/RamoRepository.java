package com.pagueibaratoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pagueibaratoapi.models.Ramo;

public interface RamoRepository extends JpaRepository<Ramo, Integer> {
    
}
