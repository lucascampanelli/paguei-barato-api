package com.pagueibaratoapi.utils.patches;

import com.pagueibaratoapi.models.requests.Sugestao;

public class PatchSugestao {

    public static Sugestao edita(Sugestao sugestaoAtual, Sugestao sugestaoEditada){
        if(sugestaoEditada.getPreco() != null)
                sugestaoAtual.setPreco(sugestaoEditada.getPreco() * 100);
        
        return sugestaoAtual;
    }

}
