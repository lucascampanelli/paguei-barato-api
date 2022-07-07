package com.pagueibaratoapi.utils;

import com.pagueibaratoapi.models.exceptions.DadosInvalidosException;
import com.pagueibaratoapi.models.requests.Produto;

public class TratamentoProduto {
    
    public static void validar(Produto produto, boolean opcional) throws DadosInvalidosException {
        
        if(produto == null)
            throw new DadosInvalidosException("corpo_nulo");

        if(produto.getId() != null)
            throw new DadosInvalidosException("id_fornecido");

        if(produto.getCor() != null && (produto.getCor().length() > 20 || produto.getCor().length() < 3))
            throw new DadosInvalidosException("cor_invalido");

        if(!opcional){
            if(produto.getNome() == null || produto.getNome().isEmpty() || produto.getNome().length() > 150 || produto.getNome().length() < 10){
                throw new DadosInvalidosException("nome_invalido");
            }
            else if(produto.getMarca() == null || produto.getMarca().isEmpty() || produto.getMarca().length() > 50 || produto.getMarca().length() <= 1){
                throw new DadosInvalidosException("marca_invalido");
            }
            else if(produto.getTamanho() == null || produto.getTamanho().isEmpty() || produto.getTamanho().length() > 20){
                throw new DadosInvalidosException("tamanho_invalido");
            }
            else if(produto.getCriadoPor() == null || produto.getCriadoPor() <= 0){
                throw new DadosInvalidosException("usuario_invalido");
            }
            else if(produto.getCategoriaId() == null || produto.getCategoriaId() <= 0){
                throw new DadosInvalidosException("categoria_invalido");
            }
        }
        else{
            if(produto.getNome() != null && (produto.getNome().isEmpty() || produto.getNome().length() > 150 || produto.getNome().length() < 10)){
                throw new DadosInvalidosException("nome_invalido");
            }
            else if(produto.getMarca() != null && (produto.getMarca().isEmpty() || produto.getMarca().length() > 50 || produto.getMarca().length() <= 1)){
                throw new DadosInvalidosException("marca_invalido");
            }
            else if(produto.getTamanho() != null && (produto.getTamanho().isEmpty() || produto.getTamanho().length() > 20)){
                throw new DadosInvalidosException("tamanho_invalido");
            }
            else if(produto.getCriadoPor() != null && produto.getCriadoPor() <= 0){
                throw new DadosInvalidosException("usuario_invalido");
            }
            else if(produto.getCategoriaId() != null && produto.getCategoriaId() <= 0){
                throw new DadosInvalidosException("categoria_invalido");
            }
        }
    }
}
