package com.pagueibaratoapi.utils.tratamentos;

import com.pagueibaratoapi.models.exceptions.DadosInvalidosException;
import com.pagueibaratoapi.models.requests.Sugestao;

public class TratamentoSugestao {
    
    public static void validar(Sugestao sugestao, boolean opcional) throws DadosInvalidosException {
        
        if(sugestao == null)
            throw new DadosInvalidosException("corpo_nulo");

        if(sugestao.getId() != null)
            throw new DadosInvalidosException("id_fornecido");

        if(!opcional){
            if(sugestao.getPreco() == null || sugestao.getPreco() < 0){
                throw new DadosInvalidosException("preco_invalido");
            }
            else if(sugestao.getCriadoPor() == null || sugestao.getCriadoPor() <= 0){
                throw new DadosInvalidosException("usuario_invalido");
            }
            else if(sugestao.getEstoqueId() == null || sugestao.getEstoqueId() <= 0){
                throw new DadosInvalidosException("estoque_invalido");
            }
        }
        else{
            if(sugestao.getPreco() != null && sugestao.getPreco() < 0){
                throw new DadosInvalidosException("preco_invalido");
            }
            else if(sugestao.getCriadoPor() != null && sugestao.getCriadoPor() <= 0){
                throw new DadosInvalidosException("usuario_invalido");
            }
            else if(sugestao.getEstoqueId() != null && sugestao.getEstoqueId() <= 0){
                throw new DadosInvalidosException("estoque_invalido");
            }
        }
    }
}
