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
     * @param numeroPagina Número da página que será retornada.
     * @param limite Quantidade de itens por página.
     * @param pagina Objeto de iteração retornado pelo JPA contendo os registros da pesquisa.
     * @return
     */
    public static ResponsePagina criarResposta(Integer numeroPagina, Integer limite, Page<?> pagina, List<?> itens) {

        ResponsePagina responsePagina = new ResponsePagina();
        
        responsePagina.setContagem((limite * (numeroPagina + 1)) - (limite - 1));
        responsePagina.setItensPorPagina(limite);
        responsePagina.setPaginaAtual(numeroPagina);
        responsePagina.setTotalPaginas(pagina.getTotalPages());
        responsePagina.setTotalRegistros(pagina.getTotalElements());
        responsePagina.setItens(itens);

        return responsePagina;

    }

}
