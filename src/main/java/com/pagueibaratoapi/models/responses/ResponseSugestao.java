package com.pagueibaratoapi.models.responses;

import java.util.Calendar;

import org.springframework.hateoas.RepresentationModel;

import com.pagueibaratoapi.models.requests.Sugestao;

public class ResponseSugestao extends RepresentationModel<ResponseSugestao> {
    
    private Integer id;
    private Float preco;
    private Calendar timestamp;

    private Integer estoqueId;

    public ResponseSugestao(Sugestao sugestao) {
        this.id = sugestao.getId();
        this.preco = sugestao.getPreco();
        this.timestamp = sugestao.getTimestamp();
        this.estoqueId = sugestao.getEstoqueId();
    }

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

    public Integer getEstoqueId() {
        return estoqueId;
    }

    public void setEstoqueId(Integer estoqueId) {
        this.estoqueId = estoqueId;
    }
    
}
