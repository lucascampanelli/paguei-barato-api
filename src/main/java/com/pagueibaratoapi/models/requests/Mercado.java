package com.pagueibaratoapi.models.requests;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "mercado")
public class Mercado {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String nome;
    private String logradouro;
    private Integer numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String uf;
    private String cep;

    @Column(name = "\"criadoPor\"")
    private Integer criadoPor;

    @Column(name = "\"ramoId\"")
    private Integer ramoId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "\"criadoPor\"", updatable = false, insertable = false)
    private Usuario usuario;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "\"ramoId\"", updatable = false, insertable = false)
    private Ramo ramo;

    @OneToMany(mappedBy = "mercado", orphanRemoval = true)
    @JsonIgnore
    private List<Estoque> estoques = new ArrayList<Estoque>();;

    public Integer getId() {
        return id;
    }

    public void setCriadoPor(Integer criadoPor) {
        this.criadoPor = criadoPor;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome.trim();
    }

    public Integer getRamoId() {
        return ramoId;
    }

    public void setRamoId(Integer ramoId) {
        this.ramoId = ramoId;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro.trim();
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        if(complemento != null)
            this.complemento = complemento.trim();
        else
            this.complemento = null;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro.trim();
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade.trim();
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf.trim().toUpperCase();
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep.trim().replaceAll("[^0-9-]", "");
    }
}
