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
@Table(name = "estoque")
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "\"criadoPor\"")
    private Integer criadoPor;
    
    @Column(name = "\"produtoId\"")
    private Integer produtoId;

    @Column(name = "\"mercadoId\"")
    private Integer mercadoId;

    @JsonIgnore
    @OneToMany(mappedBy = "estoque", orphanRemoval = true)
    private List<Sugestao> sugestoes = new ArrayList<Sugestao>();

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "\"criadoPor\"", updatable = false, insertable = false)
    private Usuario usuario;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "\"produtoId\"", updatable = false, insertable = false)
    private Produto produto;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "\"mercadoId\"", updatable = false, insertable = false)
    private Mercado mercado;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(Integer criadoPor) {
        this.criadoPor = criadoPor;
    }

    public Integer getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Integer produtoId) {
        this.produtoId = produtoId;
    }

    public Integer getMercadoId() {
        return mercadoId;
    }

    public void setMercadoId(Integer mercadoId) {
        this.mercadoId = mercadoId;
    }
}
