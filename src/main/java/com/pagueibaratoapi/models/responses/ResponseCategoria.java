package com.pagueibaratoapi.models.responses;

import org.springframework.hateoas.RepresentationModel;

import com.pagueibaratoapi.models.requests.Categoria;

public class ResponseCategoria extends RepresentationModel<ResponseCategoria> {

    private Integer id;
    private String nome;
    private String descricao;

    public ResponseCategoria(Categoria categoria) {
        this.id = categoria.getId();
        this.nome = categoria.getNome();
        this.descricao = categoria.getDescricao();
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
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
