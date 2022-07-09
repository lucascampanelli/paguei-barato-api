package com.pagueibaratoapi.utils.tratamentos;

import com.pagueibaratoapi.models.exceptions.DadosInvalidosException;
import com.pagueibaratoapi.models.requests.Usuario;

public class TratamentoUsuario {
    
    public static void validar(Usuario usuario, boolean opcional) throws DadosInvalidosException {
        if(usuario == null)
            throw new DadosInvalidosException("corpo_nulo");

        if(usuario.getId() != null)
            throw new DadosInvalidosException("id_fornecido");

        if(usuario.getComplemento() != null && (usuario.getComplemento().isEmpty() || usuario.getComplemento().length() > 20))
            throw new DadosInvalidosException("complemento_invalido");

        if(!opcional) {
            if(usuario.getNome() == null || usuario.getNome().isEmpty() || usuario.getNome().length() > 50 || usuario.getNome().length() < 3) {
                throw new DadosInvalidosException("nome_invalido");
            }
            else if(usuario.getEmail() == null || usuario.getEmail().isEmpty() || usuario.getEmail().length() > 255 || usuario.getEmail().length() < 7 || !usuario.getEmail().contains("@") || !usuario.getEmail().contains(".") || usuario.getEmail().contains(" ")) {
                throw new DadosInvalidosException("email_invalido");
            }
            else if(usuario.getSenha() == null || usuario.getSenha().isEmpty() || usuario.getSenha().length() > 255 || usuario.getSenha().length() < 8 || !usuario.getSenha().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&-+=()])(?=\\S+$).{8,20}$")) {
                throw new DadosInvalidosException("senha_invalido");
            }
            else if(usuario.getLogradouro() == null || usuario.getLogradouro().isEmpty() || usuario.getLogradouro().length() > 120 || usuario.getLogradouro().length() < 5) {
                throw new DadosInvalidosException("logradouro_invalido");
            }
            else if(usuario.getNumero() == null || usuario.getNumero() <= 0 || usuario.getNumero() > 999999) {
                throw new DadosInvalidosException("numero_invalido");
            }
            else if(usuario.getBairro() == null || usuario.getBairro().isEmpty() || usuario.getBairro().length() > 50 || usuario.getBairro().length() < 5) {
                throw new DadosInvalidosException("bairro_invalido");
            }
            else if(usuario.getCidade() == null || usuario.getCidade().isEmpty() || usuario.getCidade().length() > 30 || usuario.getCidade().length() < 5) {
                throw new DadosInvalidosException("cidade_invalido");
            }
            else if(usuario.getUf() == null || usuario.getUf().isEmpty() || !validarUf(usuario.getUf())) {
                throw new DadosInvalidosException("uf_invalido");
            }
            else if(usuario.getCep() == null || usuario.getCep().isEmpty() || usuario.getCep().length() != 9 || usuario.getCep().matches("[a-zA-Z]+$") || !usuario.getCep().contains("-")) {
                throw new DadosInvalidosException("cep_invalido");
            }
        }
        else {
            if(usuario.getNome() != null && (usuario.getNome().isEmpty() || usuario.getNome().length() > 50 || usuario.getNome().length() < 3)) {
                throw new DadosInvalidosException("nome_invalido");
            }
            else if(usuario.getEmail() != null && (usuario.getEmail().isEmpty() || usuario.getEmail().length() > 255 || usuario.getEmail().length() < 7 || !usuario.getEmail().contains("@") || !usuario.getEmail().contains(".") || usuario.getEmail().contains(" "))) {
                throw new DadosInvalidosException("email_invalido");
            }
            else if(usuario.getSenha() != null && (usuario.getSenha().isEmpty() || usuario.getSenha().length() > 255 || usuario.getSenha().length() < 8 || !usuario.getSenha().matches("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$)."))) {
                throw new DadosInvalidosException("senha_invalido");
            }
            else if(usuario.getLogradouro() != null && (usuario.getLogradouro().isEmpty() || usuario.getLogradouro().length() > 120 || usuario.getLogradouro().length() < 5)) {
                throw new DadosInvalidosException("logradouro_invalido");
            }
            else if(usuario.getNumero() != null && (usuario.getNumero() <= 0 || usuario.getNumero() > 999999)) {
                throw new DadosInvalidosException("numero_invalido");
            }
            else if(usuario.getBairro() != null && (usuario.getBairro().isEmpty() || usuario.getBairro().length() > 50 || usuario.getBairro().length() < 5)) {
                throw new DadosInvalidosException("bairro_invalido");
            }
            else if(usuario.getCidade() != null && (usuario.getCidade().isEmpty() || usuario.getCidade().length() > 30 || usuario.getCidade().length() < 5)) {
                throw new DadosInvalidosException("cidade_invalido");
            }
            else if(usuario.getUf() != null && (usuario.getUf().isEmpty() || !validarUf(usuario.getUf()))) {
                throw new DadosInvalidosException("uf_invalido");
            }
            else if(usuario.getCep() != null && (usuario.getCep().isEmpty() || usuario.getCep().length() != 9 || usuario.getCep().matches("[a-zA-Z]+$") || !usuario.getCep().contains("-"))) {
                throw new DadosInvalidosException("cep_invalido");
            }
        }
    }

    private static boolean validarUf(String uf) {

        String[] ufs = {"AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO"};

        for(String ufValida : ufs) {
            if(ufValida.equals(uf.toUpperCase()))
                return true;
        }

        return false;
    }
}
