package com.pagueibaratoapi.models;

public class Mercado {
    
    private int id;
    private int criadoPor;
    private int ramoId;
    private String nome;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String uf;
    private String cep;

    public int getId() {
        return id;
    }

    public void setCriadoPor(int criadoPor) {
        this.criadoPor = criadoPor;
    }

    private int getCriadoPor() {
        return criadoPor;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getRamoId() {
        return ramoId;
    }

    public void setRamoId(int ramoId) {
        this.ramoId = ramoId;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public Resultado create(){
        System.out.println(this.getCriadoPor());
        try {
            return new Resultado(true, "Sucesso");
        }
        catch (Exception e) {
            return new Resultado(false, e.getLocalizedMessage());
        }
    }

    public Resultado read(){
        try {
            return new Resultado(true, "Sucesso");
        }
        catch (Exception e) {
            return new Resultado(false, e.getLocalizedMessage());
        }
    }

    public Resultado update(){
        try {
            return new Resultado(true, "Sucesso");
        }
        catch (Exception e) {
            return new Resultado(false, e.getLocalizedMessage());
        }
    }

    public Resultado delete(){
        try {
            return new Resultado(true, "Sucesso");
        }
        catch (Exception e) {
            return new Resultado(false, e.getLocalizedMessage());
        }
    }
}
