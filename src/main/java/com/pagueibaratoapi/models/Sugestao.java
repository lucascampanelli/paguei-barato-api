package com.pagueibaratoapi.models;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "sugestao")
public class Sugestao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private float preco;

    @UpdateTimestamp
    private Calendar timestamp;

    @Column(name = "\"estoqueId\"")
    private int estoqueId;
    @Column(name = "\"criadoPor\"")
    private int criadoPor;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getPreco() {
        return preco;
    }

    public void setPreco(float preco) {
        this.preco = preco;
    }

    public Calendar getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Calendar timestamp) {
        this.timestamp = timestamp;
    }

    public int getEstoqueId() {
        return estoqueId;
    }

    public void setEstoqueId(int estoqueId) {
        this.estoqueId = estoqueId;
    }

    private int getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(int criadoPor) {
        this.criadoPor = criadoPor;
    }
    
}
