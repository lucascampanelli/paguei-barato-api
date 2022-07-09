package com.pagueibaratoapi.utils;

import com.pagueibaratoapi.models.requests.Categoria;

public class PatchCategoria {
    
    public static Categoria edita(Categoria categoriaAtual, Categoria categoriaEditada){
        if(categoriaEditada.getNome() != null)
            categoriaAtual.setNome(categoriaEditada.getNome());
        
        if(categoriaEditada.getDescricao() != null)
            categoriaAtual.setDescricao(categoriaEditada.getDescricao());
        
        return categoriaAtual;
    }

}
