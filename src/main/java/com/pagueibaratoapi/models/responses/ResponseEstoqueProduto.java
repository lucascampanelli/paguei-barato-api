package com.pagueibaratoapi.models.responses;

import com.pagueibaratoapi.models.requests.Produto;

/**
 * Modelo de resposta para a criação de um produto e um estoque.
 */
public class ResponseEstoqueProduto extends ResponseProduto{
    
    private int estoqueId;

    public ResponseEstoqueProduto(Produto produto) {
        super(produto);
    }

    public ResponseEstoqueProduto(Produto produto, int estoqueId) {
        super(produto);
        this.estoqueId = estoqueId;
    }

    public int getEstoqueId() {
        return estoqueId;
    }

    public void setEstoqueId(int estoqueId) {
        this.estoqueId = estoqueId;
    }
}