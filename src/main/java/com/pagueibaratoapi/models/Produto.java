package com.pagueibaratoapi.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity
@Table(name = "produto")
public class Produto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nome;
    private String marca;
    private String tamanho;
    private String cor;

    @Column(name = "\"criadoPor\"")
    private int criadoPor;
    @Column(name = "\"categoriaId\"")
    private int categoriaId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
       
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome.trim();
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca.trim();
    }

    public String getTamanho() {
        return tamanho;
    }

    public void setTamanho(String tamanho) {
        this.tamanho = tamanho.trim();
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor.trim();
    }

    private int getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(int criadoPor) {
        this.criadoPor = criadoPor;
    }

    public int getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(int categoriaId) {
        this.categoriaId = categoriaId;
    }
    
}
