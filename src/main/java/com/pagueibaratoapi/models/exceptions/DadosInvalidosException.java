package com.pagueibaratoapi.models.exceptions;

/**
 * Classe de exceção para dados inválidos.
 * Define um erro para dados que não são válidos, ou não estão de acordo com as regras.
 */
public class DadosInvalidosException extends Exception {

    // Construtor.
    public DadosInvalidosException(String message) {
        super(message);
    }
}