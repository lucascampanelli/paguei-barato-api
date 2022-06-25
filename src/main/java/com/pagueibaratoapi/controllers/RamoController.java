package com.pagueibaratoapi.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pagueibaratoapi.models.Ramo;
import com.pagueibaratoapi.repository.RamoRepository;

@RestController
@RequestMapping("/ramo")
public class RamoController {
    
    private final RamoRepository ramoRepository;

    public RamoController(RamoRepository ramoRepository) {
        this.ramoRepository = ramoRepository;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public Ramo criar(@RequestBody Ramo ramo){
        return ramoRepository.save(ramo);
    }
}
