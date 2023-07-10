package com.pagueibaratoapi.models.responses;

public class ResponseCache {
 
    public String mensagem;

    public ResponseCache(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem){
        this.mensagem = mensagem;
    }
}
