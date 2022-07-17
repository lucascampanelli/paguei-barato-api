package com.pagueibaratoapi.utils;

import com.pagueibaratoapi.models.exceptions.DadosInvalidosException;
import com.pagueibaratoapi.models.requests.Categoria;
import com.pagueibaratoapi.models.requests.Estoque;
import com.pagueibaratoapi.models.requests.Mercado;
import com.pagueibaratoapi.models.requests.Produto;
import com.pagueibaratoapi.models.requests.Ramo;
import com.pagueibaratoapi.models.requests.Sugestao;
import com.pagueibaratoapi.models.requests.Usuario;
import com.pagueibaratoapi.utils.tratamentos.TratamentoCategoria;
import com.pagueibaratoapi.utils.tratamentos.TratamentoEstoque;
import com.pagueibaratoapi.utils.tratamentos.TratamentoMercado;
import com.pagueibaratoapi.utils.tratamentos.TratamentoProduto;
import com.pagueibaratoapi.utils.tratamentos.TratamentoRamo;
import com.pagueibaratoapi.utils.tratamentos.TratamentoSugestao;
import com.pagueibaratoapi.utils.tratamentos.TratamentoUsuario;

/**
 * Classe de tratamento de dados.
 */
public class Tratamento {
    
    /**
     * Trata os dados da categoria.
     * @param categoria - Categoria a ser tratada.
     * @param opcional - Se os dados da categoria podem ser opcionais.
     * @throws DadosInvalidosException Caso algum dos dados da categoria for inválido ou não for preenchido.
     */
    public static void validarCategoria(Categoria categoria, boolean opcional) throws DadosInvalidosException {
        try{
            TratamentoCategoria.validar(categoria, opcional);
        } catch(DadosInvalidosException e){
            throw new DadosInvalidosException(e.getMessage());
        }
    }
    
    /**
     * Trata os dados do estoque.
     * @param estoque - Estoque a ser tratado.
     * @param opcional - Se os dados do estoque podem ser opcionais.
     * @throws DadosInvalidosException Caso algum dos dados do estoque for inválido ou não for preenchido.
     */
    public static void validarEstoque(Estoque estoque, boolean opcional) throws DadosInvalidosException {
        try{
            TratamentoEstoque.validar(estoque, opcional);
        } catch(DadosInvalidosException e){
            throw new DadosInvalidosException(e.getMessage());
        }
    }
    
    /**
     * Trata os dados do mercado.
     * @param mercado - Mercado a ser tratado.
     * @param opcional - Se os dados do mercado podem ser opcionais.
     * @throws DadosInvalidosException Caso algum dos dados do mercado for inválido ou não for preenchido.
     */
    public static void validarMercado(Mercado mercado, boolean opcional) throws DadosInvalidosException {
        try{
            TratamentoMercado.validar(mercado, opcional);
        } catch(DadosInvalidosException e){
            throw new DadosInvalidosException(e.getMessage());
        }
    }
    
    /**
     * Trata os dados do produto.
     * @param produto - Produto a ser tratado.
     * @param opcional - Se os dados do produto podem ser opcionais.
     * @throws DadosInvalidosException Caso algum dos dados do produto for inválido ou não for preenchido.
     */
    public static void validarProduto(Produto produto, boolean opcional) throws DadosInvalidosException {
        try{
            TratamentoProduto.validar(produto, opcional);
        } catch(DadosInvalidosException e){
            throw new DadosInvalidosException(e.getMessage());
        }
    }
    
    /**
     * Trata os dados do ramo.
     * @param ramo - Ramo a ser tratado.
     * @param opcional - Se os dados do ramo podem ser opcionais.
     * @throws DadosInvalidosException Caso algum dos dados do ramo for inválido ou não for preenchido.
     */
    public static void validarRamo(Ramo ramo, boolean opcional) throws DadosInvalidosException {
        try{
            TratamentoRamo.validar(ramo, opcional);
        } catch(DadosInvalidosException e){
            throw new DadosInvalidosException(e.getMessage());
        }
    }
    
    /**
     * Trata os dados da sugestão.
     * @param sugestao - Sugestão a ser tratada.
     * @param opcional - Se os dados da sugestão podem ser opcionais.
     * @throws DadosInvalidosException Caso algum dos dados da sugestão for inválido ou não for preenchido.
     */
    public static void validarSugestao(Sugestao sugestao, boolean opcional) throws DadosInvalidosException {
        try{
            TratamentoSugestao.validar(sugestao, opcional);
        } catch(DadosInvalidosException e){
            throw new DadosInvalidosException(e.getMessage());
        }
    }
    
    /**
     * Trata os dados do usuário.
     * @param usuario - Usuário a ser tratado.
     * @param opcional - Se os dados do usuário podem ser opcionais.
     * @throws DadosInvalidosException Caso algum dos dados do usuário for inválido ou não for preenchido.
     */
    public static void validarUsuario(Usuario usuario, boolean opcional) throws DadosInvalidosException {
        try{
            TratamentoUsuario.validar(usuario, opcional);
        } catch(DadosInvalidosException e){
            throw new DadosInvalidosException(e.getMessage());
        }
    }

    /**
     * Método responsável por verificar se um dado usuário foi removido ou não.
     * @param usuario - Usuário a ser verificado.
     * @return boolean - Retorna true caso o usuário exista, caso contrário, retorna false.
     */
    public static boolean usuarioExiste(Usuario usuario) {
        return TratamentoUsuario.existe(usuario);
    }

    /**
     * Verifica se o UF informado é um dos UFs brasileiros válidos. Este método é case insensitive, o valor passado como parâmetro é transformado em maiúsculas antes de ser verificado.
     * @param uf - String de 2 caracteres contendo o UF a ser verificado.
     * @return <b>true</b> se o UF for válido, <b>false</b> caso contrário.
     */
    public static boolean validarUf(String uf) {

        String[] ufs = {"AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO"};

        for(String ufValida : ufs) {
            if(ufValida.equals(uf.toUpperCase()))
                return true;
        }

        return false;
    }
}
