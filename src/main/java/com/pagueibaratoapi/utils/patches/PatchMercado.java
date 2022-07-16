package com.pagueibaratoapi.utils.patches;

import com.pagueibaratoapi.models.requests.Mercado;

/** Classe resposável pelo patch do Mercado */
public class PatchMercado {
    
    /**
     * Método responsável por adicionar os atributos atualizados ao estado atual do Mercado.
     * @param mercadoAtual - Objeto do Mercado no estado atual do banco de dados.
     * @param mercadoEditado - Objeto do Mercado com os atributos que serão atualizados.
     * @return Mercado - Objeto do Mercado após a inserção dos atributos atualizados.
     */
    public static Mercado edita(Mercado mercadoAtual, Mercado mercadoEditado) {
        // Se o atributo do Id do Ramo não for nulo
        if(mercadoEditado.getRamoId() != null)
            // Atribui o Id do Ramo ao objeto do Mercado atual
            mercadoAtual.setRamoId(mercadoEditado.getRamoId());

        // Se o atributo do nome do Mercado não for nulo
        if(mercadoEditado.getNome() != null)
            // Atribui o nome do Mercado ao objeto do Mercado atual
            mercadoAtual.setNome(mercadoEditado.getNome());

        // Se o atributo do logradouro do Mercado não for nulo
        if(mercadoEditado.getLogradouro() != null)
            // Atribui o logradouro do Mercado ao objeto do Mercado atual
            mercadoAtual.setLogradouro(mercadoEditado.getLogradouro());

        // Se o atribuito do número do endereço do Mercado não for nulo
        if(mercadoEditado.getNumero() != null)
            // Atribui o número do endereço do Mercado ao objeto do Mercado atual
            mercadoAtual.setNumero(mercadoEditado.getNumero());

        // Se o atributo do complemento do Mercado não for nulo
        if(mercadoEditado.getComplemento() != null) {
            // Se o atributo enviado do complemento do Mercado for vazio (aspas vazias)
            if(mercadoEditado.getComplemento().trim().isEmpty())
                // Define como nulo o atributo do complemento do Mercado do objeto do Mercado atual.
                // Isso significa que esse mercado não possui um complemento.
                mercadoAtual.setComplemento(null);
            else
                // Caso contrário, atribui o complemento do Mercado ao objeto do Mercado atual.
                mercadoAtual.setComplemento(mercadoEditado.getComplemento());
        }

        // Se o atributo do bairro do Mercado não for nulo
        if(mercadoEditado.getBairro() != null)
            // Atribui o bairro do Mercado ao objeto do Mercado atual
            mercadoAtual.setBairro(mercadoEditado.getBairro());

        // Se o atributo da cidade do Mercado não for nulo
        if(mercadoEditado.getCidade() != null)
            // Atribui a cidade do Mercado ao objeto do Mercado atual
            mercadoAtual.setCidade(mercadoEditado.getCidade());
        
        // Se o atributo do estado do Mercado não for nulo
        if(mercadoEditado.getUf() != null)
            // Atribui o estado do Mercado ao objeto do Mercado atual
            mercadoAtual.setUf(mercadoEditado.getUf());
        
        // Se o atributo do CEP do Mercado não for nulo
        if(mercadoEditado.getCep() != null)
            // Atribui o CEP do Mercado ao objeto do Mercado atual
            mercadoAtual.setCep(mercadoEditado.getCep());

        // Retorna o mercado com os atributos atualizados e os demais atributos no estado atual.
        return mercadoAtual;
    }
}