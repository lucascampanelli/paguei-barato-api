package com.pagueibaratoapi.models.responses;

public class Resultado {
    
    private boolean sucesso;
    private String mensagem;
    private Object corpo;
    
    public Resultado(
        boolean sucesso,
        String mensagem
    ){
        this.setSucesso(sucesso);
        this.setMensagem(mensagem);
    }
    
    public Resultado(
        boolean sucesso,
        String mensagem,
        Object corpo
    ){
        this.setSucesso(sucesso);
        this.setMensagem(mensagem);
        this.setCorpo(corpo);
    }

    public boolean isSucesso() {
        return sucesso;
    }

    private void setSucesso(boolean sucesso) {
        this.sucesso = sucesso;
    }

    public String getMensagem() {
        return mensagem;
    }

    private void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Object getCorpo() {
        return corpo;
    }

    private void setCorpo(Object corpo) {
        this.corpo = corpo;
    }    
}
