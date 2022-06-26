package com.pagueibaratoapi.models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "usuario")
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nome;
    private String email;
    private String senha;
    private String logradouro;
    private int numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String uf;
    private String cep;

    @OneToMany(mappedBy = "usuario", orphanRemoval = true)
    @JsonIgnore
    private List<Produto> produtos;

    @OneToMany(mappedBy = "usuario", orphanRemoval = true)
    @JsonIgnore
    private List<Mercado> mercados;

    @OneToMany(mappedBy = "usuario", orphanRemoval = true)
    @JsonIgnore
    private List<Estoque> estoques;

    @OneToMany(mappedBy = "usuario", orphanRemoval = true)
    @JsonIgnore
    private List<Sugestao> sugestoes;

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

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
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
