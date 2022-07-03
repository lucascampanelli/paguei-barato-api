package com.pagueibaratoapi.models.responses;

import org.springframework.hateoas.RepresentationModel;

import com.pagueibaratoapi.models.requests.Ramo;

public class ResponseRamo extends RepresentationModel<ResponseRamo> {
    
    private Integer id;
    private String nome;
    private String descricao;

    public ResponseRamo(Ramo ramo) {
        this.id = ramo.getId();
        this.nome = ramo.getNome();
        this.descricao = ramo.getDescricao();
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
