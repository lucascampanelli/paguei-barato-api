package com.pagueibaratoapi.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "estoque")
public class Estoque {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "\"criadoPor\"")
    private int criadoPor;
    @Column(name = "\"produtoId\"")
    private int produtoId;
    @Column(name = "\"mercadoId\"")
    private int mercadoId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(int criadoPor) {
        this.criadoPor = criadoPor;
    }

    public int getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(int produtoId) {
        this.produtoId = produtoId;
    }

    public int getMercadoId() {
        return mercadoId;
    }

    public void setMercadoId(int mercadoId) {
        this.mercadoId = mercadoId;
    }
}
