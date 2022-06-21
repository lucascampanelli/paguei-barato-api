package com.pagueibaratoapi.models;

public class Produto {
    
    private String nome;
    private String marca;
    private String peso;
    private int preco;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public int getPreco() {
        return preco;
    }
    
    public void setPreco(int preco) {
        this.preco = preco;
    }

}
