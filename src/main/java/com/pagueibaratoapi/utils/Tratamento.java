package com.pagueibaratoapi.utils;

import com.pagueibaratoapi.models.exceptions.DadosInvalidosException;
import com.pagueibaratoapi.models.requests.Categoria;
import com.pagueibaratoapi.models.requests.Estoque;
import com.pagueibaratoapi.models.requests.Mercado;
import com.pagueibaratoapi.models.requests.Produto;
import com.pagueibaratoapi.models.requests.Ramo;
import com.pagueibaratoapi.models.requests.Sugestao;
import com.pagueibaratoapi.models.requests.Usuario;
import com.pagueibaratoapi.utils.tratamentos.TratamentoCategoria;
import com.pagueibaratoapi.utils.tratamentos.TratamentoEstoque;
import com.pagueibaratoapi.utils.tratamentos.TratamentoMercado;
import com.pagueibaratoapi.utils.tratamentos.TratamentoProduto;
import com.pagueibaratoapi.utils.tratamentos.TratamentoRamo;
import com.pagueibaratoapi.utils.tratamentos.TratamentoSugestao;
import com.pagueibaratoapi.utils.tratamentos.TratamentoUsuario;

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
    
    public static void validarUsuario(Usuario usuario, boolean opcional) throws DadosInvalidosException {
        try{
            TratamentoUsuario.validar(usuario, opcional);
        } catch(DadosInvalidosException e){
            throw new DadosInvalidosException(e.getMessage());
        }
    }

    public static boolean validarUf(String uf) {

        String[] ufs = {"AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO"};

        for(String ufValida : ufs) {
            if(ufValida.equals(uf.toUpperCase()))
                return true;
        }

        return false;
    }
}
