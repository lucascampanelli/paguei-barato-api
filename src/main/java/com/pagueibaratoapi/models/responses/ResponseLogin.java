package com.pagueibaratoapi.models.responses;

import com.pagueibaratoapi.models.requests.Usuario;

public class ResponseLogin extends ResponseUsuario {
    
    private String token;

    public ResponseLogin(Usuario usuario, String token) {
        super(usuario);
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
