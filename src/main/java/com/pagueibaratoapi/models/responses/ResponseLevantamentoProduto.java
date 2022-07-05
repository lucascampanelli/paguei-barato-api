package com.pagueibaratoapi.models.responses;

import java.util.Calendar;

import com.pagueibaratoapi.models.requests.Produto;

public class ResponseLevantamentoProduto extends ResponseProduto {

    private float precoMedio;
    private float menorPreco;
    private float maiorPreco;
    private int quantidadeSugestoes;
    private Calendar dataUltimaSugestao;

    public ResponseLevantamentoProduto(Produto produto, float precoMedio, float menorPreco, float maiorPreco) {
        super(produto);
        this.precoMedio = precoMedio;
        this.menorPreco = menorPreco;
        this.maiorPreco = maiorPreco;
    }

    public ResponseLevantamentoProduto(Produto produto) {
        super(produto);
    }

    public float getPrecoMedio() {
        return precoMedio;
    }

    public void setPrecoMedio(float precoMedio) {
        this.precoMedio = precoMedio;
    }

    public float getMenorPreco() {
        return menorPreco;
    }

    public void setMenorPreco(float menorPreco) {
        this.menorPreco = menorPreco;
    }

    public float getMaiorPreco() {
        return maiorPreco;
    }

    public void setMaiorPreco(float maiorPreco) {
        this.maiorPreco = maiorPreco;
    }

    public int getQuantidadeSugestoes() {
        return quantidadeSugestoes;
    }

    public void setQuantidadeSugestoes(int quantidadeSugestoes) {
        this.quantidadeSugestoes = quantidadeSugestoes;
    }

    public Calendar getDataUltimaSugestao() {
        return dataUltimaSugestao;
    }

    public void setDataUltimaSugestao(Calendar dataUltimaSugestao) {
        this.dataUltimaSugestao = dataUltimaSugestao;
    }
    
}