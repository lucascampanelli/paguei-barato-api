package com.pagueibaratoapi.utils.patches;

import com.pagueibaratoapi.models.requests.Usuario;
import com.pagueibaratoapi.utils.Senha;

public class PatchUsuario {

    public static Usuario edita(Usuario usuarioAtual, Usuario usuarioEditado) {
        if(usuarioEditado.getNome() != null)
            usuarioAtual.setNome(usuarioEditado.getNome());
        
        if(usuarioEditado.getEmail() != null)
            usuarioAtual.setEmail(usuarioEditado.getEmail());
        
        if(usuarioEditado.getSenha() != null)
            usuarioAtual.setSenha(Senha.encriptar(usuarioEditado.getSenha()));

        if(usuarioEditado.getLogradouro() != null)
            usuarioAtual.setLogradouro(usuarioEditado.getLogradouro());

        if(usuarioEditado.getNumero() != null)
            usuarioAtual.setNumero(usuarioEditado.getNumero());

        if(usuarioEditado.getComplemento() != null) {
            if(usuarioEditado.getComplemento() == "")
                usuarioAtual.setComplemento(null);
            else
                usuarioAtual.setComplemento(usuarioEditado.getComplemento());
        }

        if(usuarioEditado.getBairro() != null)
            usuarioAtual.setBairro(usuarioEditado.getBairro());

        if(usuarioEditado.getCidade() != null)
            usuarioAtual.setCidade(usuarioEditado.getCidade());

        if(usuarioEditado.getUf() != null)
            usuarioAtual.setUf(usuarioEditado.getUf());
        
        if(usuarioEditado.getCep() != null)
            usuarioAtual.setCep(usuarioEditado.getCep());

        return usuarioAtual;
    }
}
