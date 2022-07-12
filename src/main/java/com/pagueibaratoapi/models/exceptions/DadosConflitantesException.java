package com.pagueibaratoapi.models.exceptions;

/**
 * Classe de exceção para dados conflitantes.
 * Define um erro para registros ou dados que já existem, ou possuem restrições.
 */
public class DadosConflitantesException extends Exception {
    
    // Construtor.
    public DadosConflitantesException(String message) {
        super(message);
    }
}
