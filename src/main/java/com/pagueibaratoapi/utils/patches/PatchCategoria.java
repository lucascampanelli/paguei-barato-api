package com.pagueibaratoapi.utils.patches;

import com.pagueibaratoapi.models.requests.Categoria;

/**
 * Classe resposável pelo patch da Categoria
 */
public class PatchCategoria {
    
    /**
     * Método responsável por adicionar os atributos atualizados ao estado atual da categoria.
     * @param categoriaAtual - Objeto da categoria no estado atual do banco de dados.
     * @param categoriaEditada - Objeto da categoria com os atributos que serão atualizados.
     * @return Categoria - Objeto da categoria após a inserção dos atributos atualizados.
     */
    public static Categoria edita(Categoria categoriaAtual, Categoria categoriaEditada) {
        // Se o atributo nome não for nulo
        if(categoriaEditada.getNome() != null)
            // Atribui o nome ao atributo nome da categoria atual
            categoriaAtual.setNome(categoriaEditada.getNome());
        
        // Se o atributo descrição não for nulo
        if(categoriaEditada.getDescricao() != null)
            // Atribui a descrição ao atributo descrição da categoria atual
            categoriaAtual.setDescricao(categoriaEditada.getDescricao());
        
        // Retorna a categoria com os atributos atualizados e os demais atributos no estado atual.
        return categoriaAtual;
    }
}
