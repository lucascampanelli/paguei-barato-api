package com.pagueibaratoapi.utils;

import java.util.List;

import org.springframework.data.domain.Page;

import com.pagueibaratoapi.models.responses.ResponsePagina;

/**
 * Classe utilitária para paginação de dados.
 */
public class PaginaUtils {
    
    /**
     * Método responsável por criar um objeto ResponsePagina com os dados da páginação
     * @param numeroPagina - Número da página que será retornada.
     * @param limite - Quantidade de itens por página.
     * @param pagina - Objeto de iteração retornado pelo JPA contendo os registros da pesquisa.
     * @param itens - Itens que serão enviados nessa página.
     * @return ResponsePagina - Objeto com os dados da página.
     */
    public static ResponsePagina criarResposta(Integer numeroPagina, Integer limite, Page<?> pagina, List<?> itens) {

        // Cria o objeto de resposta.
        ResponsePagina responsePagina = new ResponsePagina();
        
        // Define os dados recebidos.
        responsePagina.setContagem((limite * (numeroPagina + 1)) - (limite - 1));
        responsePagina.setItensPorPagina(limite);
        responsePagina.setPaginaAtual(numeroPagina);
        responsePagina.setTotalPaginas(pagina.getTotalPages());
        responsePagina.setTotalRegistros(pagina.getTotalElements());
        responsePagina.setItens(itens);

        // Retorna a página.
        return responsePagina;
    }
    
    /**
     * Sobrecarga do método responsável por criar um objeto ResponsePagina com os dados da páginação, sem o objeto de paginação do JPA.
     * @param numeroPagina - Número da página que será retornada.
     * @param limite - Quantidade de itens por página.
     * @param totalPaginas - Total de páginas da pesquisa.
     * @param totalRegistros - Total de registros da pesquisa.
     * @param itens - Itens que serão enviados nessa página.
     * @return ResponsePagina - Objeto com os dados da página.
     */
    public static ResponsePagina criarResposta(Integer numeroPagina, Integer limite, Integer totalPaginas, Integer totalRegistros, List<?> itens) {

        // Cria o objeto de resposta.
        ResponsePagina responsePagina = new ResponsePagina();
        
        // Define os dados recebidos.
        responsePagina.setContagem((limite * (numeroPagina + 1)) - (limite - 1));
        responsePagina.setItensPorPagina(limite);
        responsePagina.setPaginaAtual(numeroPagina);
        responsePagina.setTotalPaginas(totalPaginas);
        responsePagina.setTotalRegistros(Long.valueOf(totalRegistros));
        responsePagina.setItens(itens);

        // Retorna a página.
        return responsePagina;
    }
}
