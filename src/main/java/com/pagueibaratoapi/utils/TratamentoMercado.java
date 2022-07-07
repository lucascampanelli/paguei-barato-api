package com.pagueibaratoapi.utils;

import com.pagueibaratoapi.models.exceptions.DadosInvalidosException;
import com.pagueibaratoapi.models.requests.Mercado;

public class TratamentoMercado {
    
    public static void validar(Mercado mercado, boolean opcional) throws DadosInvalidosException {
        
        if(mercado == null)
            throw new DadosInvalidosException("corpo_nulo");

        if(mercado.getId() != null)
            throw new DadosInvalidosException("id_fornecido");

        if(mercado.getComplemento() != null && (mercado.getComplemento().isEmpty() || mercado.getComplemento().length() > 20))
            throw new DadosInvalidosException("complemento_invalido");

        if(!opcional){
            if(mercado.getNome() == null || mercado.getNome().isEmpty() || mercado.getNome().length() > 50 || mercado.getNome().length() < 5){
                throw new DadosInvalidosException("nome_invalido");
            }
            else if(mercado.getLogradouro() == null || mercado.getLogradouro().isEmpty() || mercado.getLogradouro().length() > 120 || mercado.getLogradouro().length() < 5){
                throw new DadosInvalidosException("logradouro_invalido");
            }
            else if(mercado.getNumero() == null || mercado.getNumero() <= 0 || mercado.getNumero() > 999999){
                throw new DadosInvalidosException("numero_invalido");
            }
            else if(mercado.getBairro() == null || mercado.getBairro().isEmpty() || mercado.getBairro().length() > 50 || mercado.getBairro().length() < 5){
                throw new DadosInvalidosException("bairro_invalido");
            }
            else if(mercado.getCidade() == null || mercado.getCidade().isEmpty() || mercado.getCidade().length() > 30 || mercado.getCidade().length() < 3){
                throw new DadosInvalidosException("cidade_invalido");
            }
            else if(mercado.getUf() == null || mercado.getUf().isEmpty() || !validarUf(mercado.getUf())){
                throw new DadosInvalidosException("uf_invalido");
            }
            else if(mercado.getCep() == null || mercado.getCep().isEmpty() || mercado.getCep().length() != 9 || mercado.getCep().matches("[a-zA-Z]+$") || !mercado.getCep().contains("-")){
                throw new DadosInvalidosException("cep_invalido");
            }
            else if(mercado.getCriadoPor() == null || mercado.getCriadoPor() <= 0){
                throw new DadosInvalidosException("usuario_invalido");
            }
            else if(mercado.getRamoId() == null || mercado.getRamoId() <= 0){
                throw new DadosInvalidosException("ramo_invalido");
            }
        }
        else{
            if(mercado.getNome() != null && (mercado.getNome().isEmpty() || mercado.getNome().length() > 50 || mercado.getNome().length() < 5)){
                throw new DadosInvalidosException("nome_invalido");
            }
            else if(mercado.getLogradouro() != null && (mercado.getLogradouro().isEmpty() || mercado.getLogradouro().length() > 120 || mercado.getLogradouro().length() < 5)){
                throw new DadosInvalidosException("logradouro_invalido");
            }
            else if(mercado.getNumero() != null && (mercado.getNumero() < 0 || mercado.getNumero() > 999999)){
                throw new DadosInvalidosException("numero_invalido");
            }
            else if(mercado.getBairro() != null && (mercado.getBairro().isEmpty() || mercado.getBairro().length() > 50 || mercado.getBairro().length() < 5)){
                throw new DadosInvalidosException("bairro_invalido");
            }
            else if(mercado.getCidade() != null && (mercado.getCidade().isEmpty() || mercado.getCidade().length() > 30 || mercado.getCidade().length() < 3)){
                throw new DadosInvalidosException("cidade_invalido");
            }
            else if(mercado.getUf() != null && (mercado.getUf().isEmpty() || !validarUf(mercado.getUf()))){
                throw new DadosInvalidosException("uf_invalido");
            }
            else if(mercado.getCep() != null && (mercado.getCep().isEmpty() || mercado.getCep().length() != 9 || mercado.getCep().matches("[a-zA-Z]+$") || !mercado.getCep().contains("-"))){
                throw new DadosInvalidosException("cep_invalido");
            }
            else if(mercado.getCriadoPor() != null && mercado.getCriadoPor() <= 0){
                throw new DadosInvalidosException("usuario_invalido");
            }
            else if(mercado.getRamoId() != null && mercado.getRamoId() <= 0){
                throw new DadosInvalidosException("ramo_invalido");
            }
        }
    }

    private static boolean validarUf(String uf){

        String[] ufs = {"AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO"};

        for(String ufValida : ufs){
            if(ufValida.equals(uf.toUpperCase()))
                return true;
        }

        return false;
    }
}
