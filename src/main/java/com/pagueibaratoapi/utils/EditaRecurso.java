package com.pagueibaratoapi.utils;

import com.pagueibaratoapi.models.requests.Categoria;
import com.pagueibaratoapi.models.requests.Usuario;

public class EditaRecurso {
    
    public static Usuario editarUsuario(Usuario usuarioAtual, Usuario usuarioEditado){
        return PatchUsuario.edita(usuarioAtual, usuarioEditado);
    }

    public static Categoria editarCategoria(Categoria categoriaAtual, Categoria categoriaEditada){
        return PatchCategoria.edita(categoriaAtual, categoriaEditada);
    }
    
}
