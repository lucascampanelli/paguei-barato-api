package com.pagueibaratoapi.utils;

import com.pagueibaratoapi.models.exceptions.DadosInvalidosException;
import com.pagueibaratoapi.models.requests.Categoria;

public class Tratamento {
    
    public static void validarCategoria(Categoria categoria) throws DadosInvalidosException {
        try{
            TratamentoCategoria.validar(categoria);
        } catch(DadosInvalidosException e){
            throw new DadosInvalidosException(e.getMessage());
        }
    }
}
