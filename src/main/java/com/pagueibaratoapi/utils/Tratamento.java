package com.pagueibaratoapi.utils;

import com.pagueibaratoapi.models.exceptions.DadosInvalidosException;
import com.pagueibaratoapi.models.requests.Categoria;
import com.pagueibaratoapi.models.requests.Estoque;

public class Tratamento {
    
    public static void validarCategoria(Categoria categoria, boolean opcional) throws DadosInvalidosException {
        try{
            TratamentoCategoria.validar(categoria, opcional);
        } catch(DadosInvalidosException e){
            throw new DadosInvalidosException(e.getMessage());
        }
    }
    
    public static void validarEstoque(Estoque estoque, boolean opcional) throws DadosInvalidosException {
        try{
            TratamentoEstoque.validar(estoque, opcional);
        } catch(DadosInvalidosException e){
            throw new DadosInvalidosException(e.getMessage());
        }
    }
}
