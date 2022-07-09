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

public class EditaRecurso {
    
    public static Categoria editarCategoria(Categoria categoriaAtual, Categoria categoriaEditada){
        return PatchCategoria.edita(categoriaAtual, categoriaEditada);
    }

    public static Mercado editarMercado(Mercado mercadoAtual, Mercado mercadoEditado){
        return PatchMercado.edita(mercadoAtual, mercadoEditado);
    }

    public static Produto editarProduto(Produto produtoAtual, Produto produtoEditado){
        return PatchProduto.edita(produtoAtual, produtoEditado);
    }

    public static Ramo editarRamo(Ramo ramoAtual, Ramo ramoEditado){
        return PatchRamo.edita(ramoAtual, ramoEditado);
    }

    public static Sugestao editarSugestao(Sugestao sugestaoAtual, Sugestao sugestaoEditada){
        return PatchSugestao.edita(sugestaoAtual, sugestaoEditada);
    }

    public static Usuario editarUsuario(Usuario usuarioAtual, Usuario usuarioEditado){
        return PatchUsuario.edita(usuarioAtual, usuarioEditado);
    }
    
}
