package com.pagueibaratoapi.utils.tratamentos;

import com.pagueibaratoapi.models.exceptions.DadosInvalidosException;
import com.pagueibaratoapi.models.requests.Estoque;

/**
 * Classe responsável por tratar os dados do estoque.
 */
public class TratamentoEstoque {
    
    /**
     * Método responsável por validar os dados do estoque. Lança um <code>DadosInvalidosException</code> caso os dados não sejam válidos e interrompe a execução.
     * @param estoque - Objeto do estoque a ser validado.
     * @param opcional - Indica se os dados do estoque podem ser opcionais.
     * @throws DadosInvalidosException Lançada caso os dados do estoque sejam inválidos.
     */
    public static void validar(Estoque estoque, boolean opcional) throws DadosInvalidosException {
        if(estoque == null)
            throw new DadosInvalidosException("corpo_nulo");

        if(estoque.getId() != null)
            throw new DadosInvalidosException("id_fornecido");

        if(!opcional) {
            if(estoque.getCriadoPor() == null || estoque.getCriadoPor() <= 0) {
                throw new DadosInvalidosException("usuario_invalido");
            }
            else if(estoque.getProdutoId() == null || estoque.getProdutoId() <= 0) {
                throw new DadosInvalidosException("produto_invalido");
            }
            else if(estoque.getMercadoId() == null || estoque.getMercadoId() <= 0) {
                throw new DadosInvalidosException("mercado_invalido");
            }
        }
        else {
            if(estoque.getCriadoPor() != null && estoque.getCriadoPor() <= 0) {
                throw new DadosInvalidosException("usuario_invalido");
            }
            else if(estoque.getProdutoId() != null && estoque.getProdutoId() <= 0) {
                throw new DadosInvalidosException("produto_invalido");
            }
            else if(estoque.getMercadoId() != null && estoque.getMercadoId() <= 0) {
                throw new DadosInvalidosException("mercado_invalido");
            }
        }
    }
}
