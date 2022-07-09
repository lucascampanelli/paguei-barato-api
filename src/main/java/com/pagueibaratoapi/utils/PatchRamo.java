package com.pagueibaratoapi.utils;

import com.pagueibaratoapi.models.requests.Ramo;

public class PatchRamo {

    public static Ramo edita(Ramo ramoAtual, Ramo ramoEditado){
        if(ramoEditado.getNome() != null)
            ramoAtual.setNome(ramoEditado.getNome());

        if(ramoEditado.getDescricao() != null)
            ramoAtual.setDescricao(ramoEditado.getDescricao());
        
        return ramoAtual;
    }

}
