package com.pagueibaratoapi.utils.patches;

import com.pagueibaratoapi.models.requests.Usuario;
import com.pagueibaratoapi.utils.Senha;

/**
 * Classe resposável pelo patch do Usuário
 */
public class PatchUsuario {

    /**
     * Método responsável por adicionar os atributos atualizados ao estado atual do usuário.
     * @param usuarioAtual - Objeto do usuário no estado atual do banco de dados.
     * @param usuarioEditado - Objeto do usuário com os atributos que serão atualizados.
     * @return Usuário - Objeto do usuário após a inserção dos atributos atualizados.
     */
    public static Usuario edita(Usuario usuarioAtual, Usuario usuarioEditado) {
        // Se o nome do usuário não for nulo
        if(usuarioEditado.getNome() != null)
            // Atribui o nome ao atributo nome do usuário atual.
            usuarioAtual.setNome(usuarioEditado.getNome());
        
        // Se o email do usuário não for nulo
        if(usuarioEditado.getEmail() != null)
            // Atribui o email ao atributo email do usuário atual.
            usuarioAtual.setEmail(usuarioEditado.getEmail());
        
        // Se a senha do usuário não for nula
        if(usuarioEditado.getSenha() != null)
            // Atribui a senha enviada com a criptografia ao atributo senha do usuário atual.
            usuarioAtual.setSenha(Senha.encriptar(usuarioEditado.getSenha()));

        // Se o logradouro do usuário não for nulo
        if(usuarioEditado.getLogradouro() != null)
            // Atribui o logradouro ao atributo logradouro do usuário atual.
            usuarioAtual.setLogradouro(usuarioEditado.getLogradouro());

        // Se o número do endereço do usuário não for nulo
        if(usuarioEditado.getNumero() != null)
            // Atribui o número do endereço ao atributo numero do usuário atual.
            usuarioAtual.setNumero(usuarioEditado.getNumero());

        // Se o complemento do endereço usuário não for nulo
        if(usuarioEditado.getComplemento() != null) {
            // Se o complemento do endereço do usuário for vazio (aspas vazias)
            if(usuarioEditado.getComplemento() == "")
                // Atribui null ao atributo complemento do usuário atual, haja vista que quando aspas vazias forem enviadas,
                // o complemento atual será atualizado para nulo.
                usuarioAtual.setComplemento(null);
            else
                // Atribui o complemento ao atributo complemento do usuário atual.
                usuarioAtual.setComplemento(usuarioEditado.getComplemento());
        }

        // Se o bairro do usuário não for nulo
        if(usuarioEditado.getBairro() != null)
            // Atribui o bairro ao atributo bairro do usuário atual.
            usuarioAtual.setBairro(usuarioEditado.getBairro());

        // Se a cidade do usuário não for nula
        if(usuarioEditado.getCidade() != null)
            // Atribui a cidade ao atributo cidade do usuário atual.
            usuarioAtual.setCidade(usuarioEditado.getCidade());

        // Se o estado do usuário não for nulo
        if(usuarioEditado.getUf() != null)
            // Atribui o estado ao atributo uf do usuário atual.
            usuarioAtual.setUf(usuarioEditado.getUf());
        
        // Se o cep do usuário não for nulo
        if(usuarioEditado.getCep() != null)
            // Atribui o cep ao atributo cep do usuário atual.
            usuarioAtual.setCep(usuarioEditado.getCep());

        // Retorna o usuário atualizado.
        return usuarioAtual;
    }
}
