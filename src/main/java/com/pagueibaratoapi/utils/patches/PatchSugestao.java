package com.pagueibaratoapi.utils.patches;

import com.pagueibaratoapi.models.requests.Sugestao;

/**
 * Classe resposável pelo patch da Sugestão
 */
public class PatchSugestao {

    /**
     * Método responsável por adicionar os atributos atualizados ao estado atual do ramo.
     * @param sugestaoAtual - Objeto da sugestão no estado atual do banco de dados.
     * @param sugestaoEditada - Objeto da sugestão com os atributos que serão atualizados.
     * @return Sugestao - Objeto da sugestão após a inserção dos atributos atualizados.
     */
    public static Sugestao edita(Sugestao sugestaoAtual, Sugestao sugestaoEditada) {
        // Se o preço da sugestão não for nulo
        if(sugestaoEditada.getPreco() != null)
            // Atribui o preço ao atributo preço da sugestão atual multiplicando por 100 (para remover as casas decimais e
            // salvar como inteiro).
            sugestaoAtual.setPreco(sugestaoEditada.getPreco() * 100);
        
        // Retorna a sugestão com os atributos atualizados e os demais atributos no estado atual.
        return sugestaoAtual;
    }
}
