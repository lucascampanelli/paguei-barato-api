package com.pagueibaratoapi.models.responses;

import org.springframework.hateoas.RepresentationModel;

import com.pagueibaratoapi.models.requests.Mercado;

public class ResponseMercado extends RepresentationModel<ResponseMercado> {

    private Integer id;

    private String nome;
    private String logradouro;
    private Integer numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String uf;
    private String cep;

    private Integer ramoId;

    public ResponseMercado(Mercado mercado) {
        this.id = mercado.getId();
        this.nome = mercado.getNome();
        this.logradouro = mercado.getLogradouro();
        this.numero = mercado.getNumero();
        this.complemento = mercado.getComplemento();
        this.bairro = mercado.getBairro();
        this.cidade = mercado.getCidade();
        this.uf = mercado.getUf();
        this.cep = mercado.getCep();
        this.ramoId = mercado.getRamoId();
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

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro.trim();
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        if(complemento != null)
            this.complemento = complemento.trim();
        else
            this.complemento = null;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro.trim();
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade.trim();
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf.trim().toUpperCase();
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep.trim().replaceAll("[^0-9-]", "");
    }

    public Integer getRamoId() {
        return ramoId;
    }

    public void setRamoId(Integer ramoId) {
        this.ramoId = ramoId;
    }
}
