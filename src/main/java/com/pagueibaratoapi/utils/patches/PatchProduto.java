package com.pagueibaratoapi.utils.patches;

import com.pagueibaratoapi.models.requests.Produto;

public class PatchProduto {

    public static Produto edita(Produto produtoAtual, Produto produtoEditado){
        if(produtoEditado.getNome() != null)
                produtoAtual.setNome(produtoEditado.getNome());
    
        if(produtoEditado.getMarca() != null)
            produtoAtual.setMarca(produtoEditado.getMarca());
        
        if(produtoEditado.getTamanho() != null)
            produtoAtual.setTamanho(produtoEditado.getTamanho());

        if(produtoEditado.getCor() != null){
            if(produtoEditado.getCor() == "")
                produtoAtual.setCor(null);
            else
                produtoAtual.setCor(produtoEditado.getCor());
        }

        if(produtoEditado.getCategoriaId() != null)
            produtoAtual.setCategoriaId(produtoEditado.getCategoriaId());

        return produtoAtual;
    }

}