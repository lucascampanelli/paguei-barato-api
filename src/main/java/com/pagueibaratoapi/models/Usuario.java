package com.pagueibaratoapi.models;

public class Usuario {
    
    private int id;
    private String nome;
    private String email;
    private String senha;
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

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
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
        try {
            return new Resultado(true, "Usuário criado com sucesso!");
        }
        catch (Exception e) {
            return new Resultado(false, e.getLocalizedMessage());
        }
    }

    public Resultado read(){
        try {
            return new Resultado(true, "Usuário lido com sucesso!");
        }
        catch (Exception e) {
            return new Resultado(false, e.getLocalizedMessage());
        }
    }

    public Resultado update(){
        try {
            return new Resultado(true, "Usuário atualizado com sucesso!");
        }
        catch (Exception e) {
            return new Resultado(false, e.getLocalizedMessage());
        }
    }

    public Resultado delete(){
        try {
            return new Resultado(true, "Usuário excluído com sucesso!");
        }
        catch (Exception e) {
            return new Resultado(false, e.getLocalizedMessage());
        }
    }
}
