package com.pagueibaratoapi.utils.tratamentos;

import com.pagueibaratoapi.models.exceptions.DadosInvalidosException;
import com.pagueibaratoapi.models.requests.Categoria;

/**
 * Classe responsável por tratar os dados da categoria.
 */
public class TratamentoCategoria {
    
    /**
     * Método responsável por validar os dados da categoria. Lança um <code>DadosInvalidosException</code> caso os dados não sejam válidos e interrompe a execução.
     * @param categoria - Objeto da categoria a ser validada.
     * @param opcional - Indica se os dados da categoria podem ser opcionais.
     * @throws DadosInvalidosException Lançada caso os dados da categoria sejam inválidos.
     */
    public static void validar(Categoria categoria, boolean opcional) throws DadosInvalidosException {
        if(categoria == null)
            throw new DadosInvalidosException("corpo_nulo");

        if(categoria.getId() != null)
            throw new DadosInvalidosException("id_fornecido");

        if(!opcional) {
            if(categoria.getNome() == null || categoria.getNome().isEmpty() || categoria.getNome().length() > 30) {
                throw new DadosInvalidosException("nome_invalido");
            }
            else if(categoria.getDescricao() == null || categoria.getDescricao().isEmpty() || categoria.getDescricao().length() > 150) {
                throw new DadosInvalidosException("descricao_invalido");
            }
        }
        else {
            if(categoria.getNome() != null && (categoria.getNome().isEmpty() || categoria.getNome().length() > 30)) {
                throw new DadosInvalidosException("nome_invalido");
            }
            else if(categoria.getDescricao() != null && (categoria.getDescricao().isEmpty() || categoria.getDescricao().length() > 150)) {
                throw new DadosInvalidosException("descricao_invalido");
            }
        }
    }
}
