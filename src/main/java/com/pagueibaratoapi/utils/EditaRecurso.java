package com.pagueibaratoapi.utils;

import com.pagueibaratoapi.models.requests.Categoria;
import com.pagueibaratoapi.models.requests.Mercado;
import com.pagueibaratoapi.models.requests.Usuario;

public class EditaRecurso {
    
    public static Categoria editarCategoria(Categoria categoriaAtual, Categoria categoriaEditada){
        return PatchCategoria.edita(categoriaAtual, categoriaEditada);
    }

    public static Mercado editarMercado(Mercado mercadoAtual, Mercado mercadoEditado){
        return PatchMercado.edita(mercadoAtual, mercadoEditado);
    }

    public static Usuario editarUsuario(Usuario usuarioAtual, Usuario usuarioEditado){
        return PatchUsuario.edita(usuarioAtual, usuarioEditado);
    }
    
}
