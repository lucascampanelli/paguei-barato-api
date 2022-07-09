package com.pagueibaratoapi.utils;

import com.pagueibaratoapi.models.requests.Mercado;

public class PatchMercado {
    
    public static Mercado edita(Mercado mercadoAtual, Mercado mercadoEditado){
        if(mercadoEditado.getRamoId() != null){
            mercadoAtual.setRamoId(mercadoEditado.getRamoId());
        }

        if(mercadoEditado.getNome() != null){
            mercadoAtual.setNome(mercadoEditado.getNome());
        }

        if(mercadoEditado.getLogradouro() != null)
            mercadoAtual.setLogradouro(mercadoEditado.getLogradouro());

        if(mercadoEditado.getNumero() != null)
            mercadoAtual.setNumero(mercadoEditado.getNumero());

        if(mercadoEditado.getComplemento() != null){
            if(mercadoEditado.getComplemento().trim().isEmpty())
                mercadoAtual.setComplemento(null);
            else
                mercadoAtual.setComplemento(mercadoEditado.getComplemento());
        }

        if(mercadoEditado.getBairro() != null)
            mercadoAtual.setBairro(mercadoEditado.getBairro());

        if(mercadoEditado.getCidade() != null)
            mercadoAtual.setCidade(mercadoEditado.getCidade());
            
        if(mercadoEditado.getUf() != null)
            mercadoAtual.setUf(mercadoEditado.getUf());
        
        if(mercadoEditado.getCep() != null)
            mercadoAtual.setCep(mercadoEditado.getCep());

        return mercadoAtual;
    }

}