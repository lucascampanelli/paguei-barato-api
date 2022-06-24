package com.pagueibaratoapi.models;

public class Estoque {
    
    private int id;
    private float preco;
    private int criadoPor;
    private int produtoId;
    private int mercadoId;

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
