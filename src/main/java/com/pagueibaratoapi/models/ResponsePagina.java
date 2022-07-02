package com.pagueibaratoapi.models;

import java.util.List;

import org.springframework.hateoas.RepresentationModel;

public class ResponsePagina extends RepresentationModel<ResponsePagina> {
    
    private Integer contagem;
    private Integer itensPorPagina;
    private Integer paginaAtual;
    private Integer totalPaginas;
    private Long totalRegistros;
    private List<?> itens;

    public Integer getContagem() {
        return contagem;
    }

    public void setContagem(Integer contagem) {
        this.contagem = contagem;
    }

    public Integer getItensPorPagina() {
        return itensPorPagina;
    }

    public void setItensPorPagina(Integer itensPorPagina) {
        this.itensPorPagina = itensPorPagina;
    }
    
    public Integer getPaginaAtual() {
        return paginaAtual;
    }

    public void setPaginaAtual(Integer paginaAtual) {
        this.paginaAtual = paginaAtual;
    }

    public Integer getTotalPaginas() {
        return totalPaginas;
    }

    public void setTotalPaginas(Integer totalPaginas) {
        this.totalPaginas = totalPaginas;
    }

    public Long getTotalRegistros() {
        return totalRegistros;
    }

    public void setTotalRegistros(Long totalRegistros) {
        this.totalRegistros = totalRegistros;
    }

    public List<?> getItens() {
        return itens;
    }

    public void setItens(List<?> itens) {
        this.itens = itens;
    }
}
