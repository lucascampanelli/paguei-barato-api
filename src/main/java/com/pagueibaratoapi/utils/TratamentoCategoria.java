package com.pagueibaratoapi.utils;

import com.pagueibaratoapi.models.exceptions.DadosInvalidosException;
import com.pagueibaratoapi.models.requests.Categoria;

public class TratamentoCategoria {
    
    public static void validar(Categoria categoria, boolean opcional) throws DadosInvalidosException {

        if(categoria == null)
            throw new DadosInvalidosException("corpo_nulo");

        if(categoria.getId() != null)
            throw new DadosInvalidosException("id_fornecido");

        if(!opcional){
            if(categoria.getNome() == null || categoria.getNome().isEmpty() || categoria.getNome().length() > 30){
                throw new DadosInvalidosException("nome_invalido");
            }
            else if(categoria.getDescricao() == null || categoria.getDescricao().isEmpty() || categoria.getDescricao().length() > 150){
                throw new DadosInvalidosException("descricao_invalido");
            }
        }
        else{
            if(categoria.getNome() != null && (categoria.getNome().isEmpty() || categoria.getNome().length() > 30)){
                throw new DadosInvalidosException("nome_invalido");
            }
            else if(categoria.getDescricao() != null && (categoria.getDescricao().isEmpty() || categoria.getDescricao().length() > 150)){
                throw new DadosInvalidosException("descricao_invalido");
            }
        }
    }
}
