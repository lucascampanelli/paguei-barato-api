package com.pagueibaratoapi.models;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "sugestao")
public class Sugestao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Float preco;

    @UpdateTimestamp
    private Calendar timestamp;

    @Column(name = "\"criadoPor\"")
    private Integer criadoPor;

    @Column(name = "\"estoqueId\"")
    private Integer estoqueId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "\"estoqueId\"", updatable = false, insertable = false)
    private Estoque estoque;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "\"criadoPor\"", updatable = false, insertable = false)
    private Usuario usuario;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getPreco() {
        return preco;
    }

    public void setPreco(Float preco) {
        this.preco = preco;
    }

    public Calendar getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Calendar timestamp) {
        this.timestamp = timestamp;
    }

    private Integer getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(Integer criadoPor) {
        this.criadoPor = criadoPor;
    }

    public Integer getEstoqueId() {
        return estoqueId;
    }

    public void setEstoqueId(Integer estoqueId) {
        this.estoqueId = estoqueId;
    }
    
}
