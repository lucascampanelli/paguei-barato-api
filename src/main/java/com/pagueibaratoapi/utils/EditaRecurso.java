package com.pagueibaratoapi.utils;

import com.pagueibaratoapi.models.requests.Categoria;
import com.pagueibaratoapi.models.requests.Mercado;
import com.pagueibaratoapi.models.requests.Produto;
import com.pagueibaratoapi.models.requests.Ramo;
import com.pagueibaratoapi.models.requests.Sugestao;
import com.pagueibaratoapi.models.requests.Usuario;
import com.pagueibaratoapi.utils.patches.PatchCategoria;
import com.pagueibaratoapi.utils.patches.PatchMercado;
import com.pagueibaratoapi.utils.patches.PatchProduto;
import com.pagueibaratoapi.utils.patches.PatchRamo;
import com.pagueibaratoapi.utils.patches.PatchSugestao;
import com.pagueibaratoapi.utils.patches.PatchUsuario;

/**
 * Classe utilitária para realização do PATCH (alteração parcial) nos recursos.
 */
public class EditaRecurso {
    
    /**
     * Método para atribuir novos dados a uma categoria a fim de editá-la.
     * @param categoriaAtual - Categoria que será editada, com os dados originais.
     * @param categoriaEditada - Objeto de categoria com os dados novos.
     * @return Objeto de categoria com os dados atualizados.
     */
    public static Categoria editarCategoria(Categoria categoriaAtual, Categoria categoriaEditada) {
        return PatchCategoria.edita(categoriaAtual, categoriaEditada);
    }

    /**
     * Método para atribuir novos dados a um mercado a fim de editá-lo.
     * @param mercadoAtual - Mercado que será editado, com os dados originais.
     * @param mercadoEditado - Objeto de mercado com os dados novos.
     * @return Objeto de mercado com os dados atualizados.
     */
    public static Mercado editarMercado(Mercado mercadoAtual, Mercado mercadoEditado) {
        return PatchMercado.edita(mercadoAtual, mercadoEditado);
    }

    /**
     * Método para atribuir novos dados a um produto a fim de editá-lo.
     * @param produtoAtual - Produto que será editado, com os dados originais.
     * @param produtoEditado - Objeto de produto com os dados novos.
     * @return Objeto de produto com os dados atualizados.
     */
    public static Produto editarProduto(Produto produtoAtual, Produto produtoEditado) {
        return PatchProduto.edita(produtoAtual, produtoEditado);
    }

    /**
     * Método para atribuir novos dados a um ramo a fim de editá-lo.
     * @param ramoAtual - Ramo que será editado, com os dados originais.
     * @param ramoEditado - Objeto de ramo com os dados novos.
     * @return Objeto de ramo com os dados atualizados.
     */
    public static Ramo editarRamo(Ramo ramoAtual, Ramo ramoEditado) {
        return PatchRamo.edita(ramoAtual, ramoEditado);
    }

    /**
     * Método para atribuir novos dados a uma sugestão a fim de editá-la.
     * @param sugestaoAtual - Sugestão que será editada, com os dados originais.
     * @param sugestaoEditada - Objeto de sugestão com os dados novos.
     * @return Objeto de sugestão com os dados atualizados.
     */
    public static Sugestao editarSugestao(Sugestao sugestaoAtual, Sugestao sugestaoEditada) {
        return PatchSugestao.edita(sugestaoAtual, sugestaoEditada);
    }

    /**
     * Método para atribuir novos dados a um usuário a fim de editá-lo.
     * @param usuarioAtual - Usuário que será editado, com os dados originais.
     * @param usuarioEditado - Objeto de usuário com os dados novos.
     * @return Objeto de usuário com os dados atualizados.
     */
    public static Usuario editarUsuario(Usuario usuarioAtual, Usuario usuarioEditado) {
        return PatchUsuario.edita(usuarioAtual, usuarioEditado);
    }
}
