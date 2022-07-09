package com.pagueibaratoapi.models.responses;

import org.springframework.hateoas.RepresentationModel;

import com.pagueibaratoapi.models.requests.Estoque;

public class ResponseEstoque extends RepresentationModel<ResponseEstoque> {

    private Integer id;

    private Integer criadoPor;
    private Integer produtoId;
    private Integer mercadoId;

    public ResponseEstoque(Estoque estoque) {
        this.id = estoque.getId();
        this.criadoPor = estoque.getCriadoPor();
        this.produtoId = estoque.getProdutoId();
        this.mercadoId = estoque.getMercadoId();
    }

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
