package com.pagueibaratoapi.utils;

import com.pagueibaratoapi.models.exceptions.DadosInvalidosException;
import com.pagueibaratoapi.models.requests.Categoria;
import com.pagueibaratoapi.models.requests.Estoque;
import com.pagueibaratoapi.models.requests.Mercado;
import com.pagueibaratoapi.models.requests.Produto;
import com.pagueibaratoapi.models.requests.Ramo;
import com.pagueibaratoapi.models.requests.Sugestao;

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
    
    public static void validarMercado(Mercado mercado, boolean opcional) throws DadosInvalidosException {
        try{
            TratamentoMercado.validar(mercado, opcional);
        } catch(DadosInvalidosException e){
            throw new DadosInvalidosException(e.getMessage());
        }
    }
    
    public static void validarProduto(Produto produto, boolean opcional) throws DadosInvalidosException {
        try{
            TratamentoProduto.validar(produto, opcional);
        } catch(DadosInvalidosException e){
            throw new DadosInvalidosException(e.getMessage());
        }
    }
    
    public static void validarRamo(Ramo ramo, boolean opcional) throws DadosInvalidosException {
        try{
            TratamentoRamo.validar(ramo, opcional);
        } catch(DadosInvalidosException e){
            throw new DadosInvalidosException(e.getMessage());
        }
    }
    
    public static void validarSugestao(Sugestao sugestao, boolean opcional) throws DadosInvalidosException {
        try{
            TratamentoSugestao.validar(sugestao, opcional);
        } catch(DadosInvalidosException e){
            throw new DadosInvalidosException(e.getMessage());
        }
    }
}
