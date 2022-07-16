package com.pagueibaratoapi.utils.patches;

import com.pagueibaratoapi.models.requests.Ramo;

/** Classe resposável pelo patch do Ramo */
public class PatchRamo {

    /**
     * Método responsável por adicionar os atributos atualizados ao estado atual do ramo.
     * @param ramoAtual - Objeto do ramo no estado atual do banco de dados.
     * @param ramoEditado - Objeto do ramo com os atributos que serão atualizados.
     * @return Ramo - Objeto do ramo após a inserção dos atributos atualizados.
     */
    public static Ramo edita(Ramo ramoAtual, Ramo ramoEditado) {
        // Se o atributo nome não for nulo
        if(ramoEditado.getNome() != null)
            // Atribui o nome ao atributo nome do ramo atual
            ramoAtual.setNome(ramoEditado.getNome());

        // Se o atributo descrição não for nulo
        if(ramoEditado.getDescricao() != null)
            // Atribui a descrição ao atributo descrição do ramo atual
            ramoAtual.setDescricao(ramoEditado.getDescricao());
        
        // Retorna o ramo com os atributos atualizados e os demais atributos no estado atual.
        return ramoAtual;
    }
}
