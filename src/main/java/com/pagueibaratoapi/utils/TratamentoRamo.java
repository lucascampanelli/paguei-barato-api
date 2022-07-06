package com.pagueibaratoapi.utils;

import com.pagueibaratoapi.models.exceptions.DadosInvalidosException;
import com.pagueibaratoapi.models.requests.Ramo;

public class TratamentoRamo {
    
    public static void validar(Ramo ramo, boolean opcional) throws DadosInvalidosException {
        
        if(ramo == null)
            throw new DadosInvalidosException("corpo_nulo");

        if(ramo.getId() != null)
            throw new DadosInvalidosException("id_fornecido");

        if(!opcional){
            if(ramo.getNome() == null || ramo.getNome().isEmpty() || ramo.getNome().length() > 30 || ramo.getNome().length() < 3){
                throw new DadosInvalidosException("nome_invalido");
            }
            else if(ramo.getDescricao() == null || ramo.getDescricao().isEmpty() || ramo.getDescricao().length() > 150 || ramo.getDescricao().length() < 10){
                throw new DadosInvalidosException("descricao_invalido");
            }
        }
        else{
            if(ramo.getNome() != null && (ramo.getNome().isEmpty() || ramo.getNome().length() > 30 || ramo.getNome().length() < 3)){
                throw new DadosInvalidosException("nome_invalido");
            }
            else if(ramo.getDescricao() != null && (ramo.getDescricao().isEmpty() || ramo.getDescricao().length() > 150 || ramo.getDescricao().length() < 10)){
                throw new DadosInvalidosException("descricao_invalido");
            }
        }
    }
}
