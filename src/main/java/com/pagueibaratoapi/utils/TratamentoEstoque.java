package com.pagueibaratoapi.utils;

import com.pagueibaratoapi.models.exceptions.DadosInvalidosException;
import com.pagueibaratoapi.models.requests.Estoque;

public class TratamentoEstoque {
    
    public static void validar(Estoque estoque, boolean opcional) throws DadosInvalidosException {
        
        if(estoque == null)
            throw new DadosInvalidosException("corpo_nulo");

        if(estoque.getId() != null)
            throw new DadosInvalidosException("id_fornecido");

        if(!opcional){
            if(estoque.getCriadoPor() == null || estoque.getCriadoPor() <= 0){
                throw new DadosInvalidosException("usuario_invalido");
            }
            else if(estoque.getProdutoId() == null || estoque.getProdutoId() <= 0){
                throw new DadosInvalidosException("produto_invalido");
            }
            else if(estoque.getMercadoId() == null || estoque.getMercadoId() <= 0){
                throw new DadosInvalidosException("mercado_invalido");
            }
        }
        else{
            if(estoque.getCriadoPor() != null && estoque.getCriadoPor() <= 0){
                throw new DadosInvalidosException("usuario_invalido");
            }
            else if(estoque.getProdutoId() != null && estoque.getProdutoId() <= 0){
                throw new DadosInvalidosException("produto_invalido");
            }
            else if(estoque.getMercadoId() != null && estoque.getMercadoId() <= 0){
                throw new DadosInvalidosException("mercado_invalido");
            }
        }
    }
}
