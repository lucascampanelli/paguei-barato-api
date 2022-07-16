package com.pagueibaratoapi.utils.patches;

import com.pagueibaratoapi.models.requests.Produto;

/** Classe responsável pelo patch do Produto */
public class PatchProduto {

    /**
     * Método responsável por adicionar os atributos atualizados ao estado atual do produto.
     * @param produtoAtual - Objeto do produto no estado atual do banco de dados.
     * @param produtoEditado - Objeto do produto com os atributos que serão atualizados.
     * @return Produto - Objeto do produto após a inserção dos atributos atualizados.
     */
    public static Produto edita(Produto produtoAtual, Produto produtoEditado) {
        // Se o atributo nome não for nulo
        if(produtoEditado.getNome() != null)
            // Atribui o nome ao atributo nome do produto atual
            produtoAtual.setNome(produtoEditado.getNome());
        
        // Se o atributo marca não for nulo
        if(produtoEditado.getMarca() != null)
            // Atribui a marca ao atributo marca do produto atual
            produtoAtual.setMarca(produtoEditado.getMarca());
        
        // Se o atributo tamanho não for nulo
        if(produtoEditado.getTamanho() != null)
            // Atribui o tamanho ao atributo tamanho do produto atual
            produtoAtual.setTamanho(produtoEditado.getTamanho());

        // Se o atributo cor não for nulo
        if(produtoEditado.getCor() != null) {
            // Se o atributo cor enviado for vazio (aspas vazias)
            if(produtoEditado.getCor() == "")
                // Atribui null ao atributo cor do produto atual, haja vista que a cor pode ser nula no banco dados
                produtoAtual.setCor(null);
            else
                // Atribui a cor ao atributo cor do produto atual
                produtoAtual.setCor(produtoEditado.getCor());
        }

        // Se o atributo do id da categoria não for nulo
        if(produtoEditado.getCategoriaId() != null)
            // Atribui o id da categoria ao atributo do id da categoria do produto atual
            produtoAtual.setCategoriaId(produtoEditado.getCategoriaId());

        // Retorna o produto com os atributos atualizados e os demais atributos no estado atual.
        return produtoAtual;
    }
}