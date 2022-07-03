package com.pagueibaratoapi.models.responses;

import org.springframework.hateoas.RepresentationModel;

import com.pagueibaratoapi.models.requests.Produto;

public class ResponseProduto extends RepresentationModel<ResponseProduto> {
    
    private Integer id;
    private String nome;
    private String marca;
    private String tamanho;
    private String cor;

    private Integer categoriaId;

    public ResponseProduto(Produto produto) {
        this.id = produto.getId();
        this.nome = produto.getNome();
        this.marca = produto.getMarca();
        this.tamanho = produto.getTamanho();
        this.cor = produto.getCor();
        this.categoriaId = produto.getCategoriaId();
    }

    public Integer getId() {
        return id;
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

    public Integer getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Integer categoriaId) {
        this.categoriaId = categoriaId;
    }
    
}
