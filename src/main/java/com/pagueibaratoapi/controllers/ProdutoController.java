package com.pagueibaratoapi.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.pagueibaratoapi.models.exceptions.DadosConflitantesException;
import com.pagueibaratoapi.models.exceptions.DadosInvalidosException;
import com.pagueibaratoapi.models.requests.Estoque;
import com.pagueibaratoapi.models.requests.Mercado;
import com.pagueibaratoapi.models.requests.Produto;
import com.pagueibaratoapi.models.requests.Sugestao;
import com.pagueibaratoapi.models.requests.Usuario;
import com.pagueibaratoapi.models.responses.ResponseLevantamentoProduto;
import com.pagueibaratoapi.models.responses.ResponseMercado;
import com.pagueibaratoapi.models.responses.ResponsePagina;
import com.pagueibaratoapi.models.responses.ResponseProduto;
import com.pagueibaratoapi.repository.CategoriaRepository;
import com.pagueibaratoapi.repository.EstoqueRepository;
import com.pagueibaratoapi.repository.MercadoRepository;
import com.pagueibaratoapi.repository.ProdutoRepository;
import com.pagueibaratoapi.repository.SugestaoRepository;
import com.pagueibaratoapi.repository.UsuarioRepository;
import com.pagueibaratoapi.utils.EditaRecurso;
import com.pagueibaratoapi.utils.PaginaUtils;
import com.pagueibaratoapi.utils.Tratamento;

/**
 * Classe responsável por controlar as requisições de produtos.
 */
@RestController
@RequestMapping("/produto")
public class ProdutoController {

    // Repositórios responsável pelos métodos JPA dp banco de dados.
    private final CategoriaRepository categoriaRepository;
    private final EstoqueRepository estoqueRepository;
    private final MercadoRepository mercadoRepository;
    private final ProdutoRepository produtoRepository;
    private final SugestaoRepository sugestaoRepository;
    private final UsuarioRepository usuarioRepository;

    // Construtor
    public ProdutoController(
        CategoriaRepository categoriaRepository,
        EstoqueRepository estoqueRepository,
        MercadoRepository mercadoRepository,
        ProdutoRepository produtoRepository,
        SugestaoRepository sugestaoRepository,
        UsuarioRepository usuarioRepository
    ) {
        this.categoriaRepository = categoriaRepository;
        this.estoqueRepository = estoqueRepository;
        this.mercadoRepository = mercadoRepository;
        this.produtoRepository = produtoRepository;
        this.sugestaoRepository = sugestaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Rota responsável por criar um produto.
     * @param categoria - Dados do produto a ser criado.
     * @return Dados e id do produto criado.
     */
    @PostMapping
    @CacheEvict(value = "Produtos", allEntries = true)
    public ResponseProduto criar(@RequestBody Produto requestProduto, @RequestHeader(value = "Authorization", required = true) String authorization) {
        try {
            System.out.println("Auth: " + authorization);
            DecodedJWT authToken = JWT.decode(authorization.substring(7));
            System.out.println(authToken);
            String idUsuario = authToken.getSubject();
            System.out.println(idUsuario);

            // Valida os dados fornecidos.
            Tratamento.validarProduto(requestProduto, false);

            // Se o usuário fornecido não existir,
            if(!usuarioRepository.existsById(requestProduto.getCriadoPor()))
                // Retorna um erro.
                throw new DadosInvalidosException("usuario_nao_encontrado");
            
            // Se a categoria fornecida não existir,
            if(!categoriaRepository.existsById(requestProduto.getCategoriaId()))
                // Retorna um erro.
                throw new DadosInvalidosException("categoria_nao_encontrado");

            // Se as características fornecidas pertencer a um produto já existente,
            if(produtoRepository.findByCaracteristicas(
                requestProduto.getNome(), 
                requestProduto.getMarca(),
                requestProduto.getTamanho(), 
                requestProduto.getCor()) != null
            )
                // Retorna um erro.
                throw new DadosConflitantesException("produto_existente");

            // Buscando o usuário informado como criador do produto.
            Usuario usuario = usuarioRepository.findById(requestProduto.getCriadoPor()).get();

            // Verifica se o usuário informado como criador não foi deletado anteriormente.
            if(!Tratamento.usuarioExiste(usuario))
                // Retorna um erro.
                throw new NoSuchElementException("usuario_nao_encontrado");

            // Criando uma nova instância do produto para tratar o nome dele e criá-lo no banco.
            Produto produtoTratado = requestProduto;

            // Pegando cada palavra do nome do produto em minúsculas separado por espaço.
            String[] nomeProduto = requestProduto.getNome().toLowerCase().split(" ");

            // Percorrendo cada palavra do nome do produto
            for(int i = 0; i < nomeProduto.length; i++) {
                // Transformando a palavra atual em uma array
                char[] palavraArr = nomeProduto[i].toCharArray();

                // Transformando a primeira letra da palavra em maiúscula
                palavraArr[0] = nomeProduto[i].toUpperCase().charAt(0);

                // Reescreve a palavra atual com a primeira letra tratada
                nomeProduto[i] = String.valueOf(palavraArr);

                // Se for a primeira palavra sendo tratada,
                // substitui o nome do produto pelo nome tratado
                if(i < 1)
                    produtoTratado.setNome(nomeProduto[i]);
                // Se não, concatena a palavra atual ao nome do produto
                else
                    produtoTratado.setNome(produtoTratado.getNome() + " " + nomeProduto[i]);
            }

            // Insere o produto e transforma os dados obtidos em modelo de resposta.
            ResponseProduto responseProduto = new ResponseProduto(produtoRepository.save(produtoTratado));

            // Adiciona o link para o produto.
            responseProduto.add(
                linkTo(
                    methodOn(ProdutoController.class).ler(responseProduto.getId())
                )
                .withSelfRel()
            );

            // Retorna os dados do produto criado.
            return responseProduto;

        } catch (DadosConflitantesException e) {
            throw new ResponseStatusException(409, e.getMessage(), e);
        } catch(NoSuchElementException e) {
            throw new ResponseStatusException(404, e.getMessage(), e);
        } catch (DadosInvalidosException e) {
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(500, "erro_insercao", e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Rota responsável por ler um produto.
     * @param id - Id do produto a ser lido.
     * @return Dados do produto.
     */
    @GetMapping("/{id}")
    public ResponseProduto ler(@PathVariable("id") Integer id) {
        try {

            // Busca o produto no banco e transforma os dados obtidos em modelo de resposta.
            ResponseProduto responseProduto = new ResponseProduto(produtoRepository.findById(id).get());

            // Adiciona o link para a rota de listagem de produtos.
            if(responseProduto != null) {
                responseProduto.add(
                    linkTo(
                        methodOn(ProdutoController.class).listar(new Produto())
                    )
                    .withRel("collection")
                );
            }

            // Retorna os dados do produto.
            return responseProduto;

        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Rota responsável por listar todos os mercados onde o produto pode ser encontrado.
     * @param id - Id do produto a ser buscado como parâmetro.
     * @return <b>List< ResponseMercado ></b> - Lista de mercados onde o produto pode ser encontrado.
     */
    @GetMapping("/{id}/mercado")
    @Cacheable("produtoMercados")
    public List<ResponseMercado> listarMercados(@PathVariable("id") Integer id) {
        try {

            // Buscando os estoques que possuem o produto informado e armazenando numa lista.
            List<Estoque> estoques = estoqueRepository.findByProdutoId(id);

            // Busca o produto no banco e transforma os dados obtidos em modelo de resposta.
            List<ResponseMercado> responseMercado = new ArrayList<>();

            // Percorrendo cada estoque encontrado
            for(Estoque estoque : estoques){
                // Armazenando o mercado do estoque na lista de resposta com os mercados encontrados.
                responseMercado.add(
                    // Chamando o construtor da classe ResponseMercado que converte o objeto Mercado em modelo de resposta.
                    new ResponseMercado(
                        // Buscando o mercado pelo ID.
                        mercadoRepository.findById(
                            // Passando como parâmetro o ID do mercado no estoque atual.
                            estoque.getMercadoId()
                        )
                        .get()
                    )
                );
            }

            // Se não houver nenhum mercado que possua o produto
            if(responseMercado.isEmpty())
                // Lança uma exceção informando que não foi encontrado um estoque com o produto informado.
                throw new NoSuchElementException("estoque_nao_encontrado");

            // Para cada mercado da lista de resposta.
            for(ResponseMercado mercado : responseMercado) {
                // Adiciona ao mercado o link para a leitura do mesmo.
                mercado.add(
                    linkTo(
                        methodOn(MercadoController.class).ler(mercado.getId())
                    )
                    .withSelfRel()
                );                

                // Adiciona ao mercado o link para a rota de listagem de mercados (coleção).
                mercado.add(
                    linkTo(
                        methodOn(MercadoController.class).listar(new Mercado())
                    )
                    .withRel("collection")
                );
            }

            // Retorna os dados do produto.
            return responseMercado;

        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Sobrecarga do método responsável por listar todos os mercados onde o produto pode ser encontrado. Pode ser paginado.
     * @param id - Id do produto a ser buscado como parâmetro.
     * @param pagina - Número da página que será listada.
     * @param limite - Número de registros que serão listados por página.
     * @return <b>ResponsePagina</b> - Objeto com as informações da página e a lista de mercados onde o produto pode ser encontrado.
     */
    @GetMapping(value = "/{id}/mercado", params = {"pagina", "limite"})
    @Cacheable("produtoMercados")
    public ResponsePagina listarMercados(
        @PathVariable("id") Integer id,
        @RequestParam(required = false, defaultValue = "0") Integer pagina,
        @RequestParam(required = false, defaultValue = "10") Integer limite
    ) {
        try {

            // Buscando os estoques que possuem o produto informado e armazenando numa lista.
            List<Estoque> estoques = estoqueRepository.findByProdutoId(id);

            // Busca o produto no banco e transforma os dados obtidos em modelo de resposta.
            List<ResponseMercado> mercados = new ArrayList<>();

            // Percorrendo cada estoque encontrado
            for(Estoque estoque : estoques){
                // Armazenando o mercado do estoque na lista de resposta com os mercados encontrados.
                mercados.add(
                    // Chamando o construtor da classe ResponseMercado que converte o objeto Mercado em modelo de resposta.
                    new ResponseMercado(
                        // Buscando o mercado pelo ID.
                        mercadoRepository.findById(
                            // Passando como parâmetro o ID do mercado no estoque atual.
                            estoque.getMercadoId()
                        )
                        .get()
                    )
                );
            }

            // Se não houver nenhum mercado que possua o produto
            if(mercados.isEmpty())
                // Lança uma exceção informando que não foi encontrado um estoque com o produto informado.
                throw new NoSuchElementException("estoque_nao_encontrado");

            // Mercados que serão mostradas na página atual.
            // O início da página é a multiplicação do número da página pelo limite de mercados por página.
            // O fim da página é o inicío da página mais o limite de mercados por página.
            List<ResponseMercado> mercadosPagina = mercados.subList(pagina * limite, (pagina * limite) + limite);

            // Representa o total de páginas da pesquisa.
            // O total de páginas é calculado dividindo o total de mercados pelo limite de mercados por página.
            // O valor é arredondado para cima porque existe a possibilidade da ultima página não possuir o número limite de mercados.
            Integer totalPaginas = (int) Math.ceil(mercados.size() / (double) limite);

            // Total de mercados da pesquisa.
            // Se o tamanho for maior que Integer.MAX_VALUE, o valor é Integer.MAX_VALUE.
            Integer totalRegistros = mercados.size();

            // Para cada mercado da lista de resposta.
            for(ResponseMercado mercado : mercados) {
                // Adiciona ao mercado o link para a leitura do mesmo.
                mercado.add(
                    linkTo(
                        methodOn(MercadoController.class).ler(mercado.getId())
                    )
                    .withSelfRel()
                );                

                // Adiciona ao mercado o link para a rota de listagem de mercados (coleção).
                mercado.add(
                    linkTo(
                        methodOn(MercadoController.class).listar(new Mercado())
                    )
                    .withRel("collection")
                );
            }

            // Prepara uma resposta em formato de página
            ResponsePagina responseMercado = PaginaUtils.criarResposta(pagina, limite, totalRegistros, totalPaginas, mercadosPagina);

            // Adiciona à resposta um link para a primeira página da listagem de mercados.
            responseMercado.add(
                linkTo(
                    methodOn(ProdutoController.class).listarMercados(id, 0, limite)
                )
                .withRel("first")
            );

            // Se a página de mercados não estiver vazia.
            if(!mercados.isEmpty()) {
                // Se a página informada pelo cliente não for a primeira página da listagem de mercados.
                if(pagina > 0) {
                    // Adiciona à resposta um link para a página anterior da listagem de mercados.
                    responseMercado.add(
                        linkTo(
                            methodOn(ProdutoController.class).listarMercados(id, pagina - 1, limite)
                        )
                        .withRel("previous")
                    );
                }

                // Se a página informada pelo cliente não for a última página da listagem de mercados.
                if(pagina < totalPaginas - 1) {
                    // Adiciona à resposta um link para a página seguinte da listagem de mercados.
                    responseMercado.add(
                        linkTo(
                            methodOn(ProdutoController.class).listarMercados(id, pagina + 1, limite)
                        )
                        .withRel("next")
                    );
                }

                // Adiciona à resposta um link para a última página da listagem de mercados.
                responseMercado.add(
                    linkTo(
                        methodOn(ProdutoController.class).listarMercados(id, totalPaginas - 1, limite)
                    )
                    .withRel("last")
                );
            }

            // Retorna os dados do produto.
            return responseMercado;

        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Sobrecarga do método responsável por listar todos os mercados onde o produto pode ser encontrado. Pode ser ordenado.
     * @param id - Id do produto a ser buscado como parâmetro.
     * @param ordenarPor - Campo do banco de dados que servirá de parâmetro para ordenar.
     * @param ordem - Direção em que os dados serão ordenados entre "asc" e "desc".
     * @return <b>List < ResponseMercado ></b> - Lista de mercados onde o produto pode ser encontrado ordenada.
     */
    @GetMapping(value = "/{id}/mercado", params = {"ordenarPor", "ordem"})
    @Cacheable("produtoMercados")
    public List<ResponseMercado> listarMercados(
        @PathVariable("id") Integer id,
        @RequestParam(required = false, defaultValue = "") String ordenarPor,
        @RequestParam(required = false, defaultValue = "asc") String ordem
    ) {
        try {

            // Buscando os estoques que possuem o produto informado e armazenando numa lista.
            List<Estoque> estoques = estoqueRepository.findByProdutoId(id);

            // Busca o produto no banco e transforma os dados obtidos em modelo de resposta.
            List<ResponseMercado> responseMercado = new ArrayList<>();

            // Percorrendo cada estoque encontrado
            for(Estoque estoque : estoques){
                // Armazenando o mercado do estoque na lista de resposta com os mercados encontrados.
                responseMercado.add(
                    // Chamando o construtor da classe ResponseMercado que converte o objeto Mercado em modelo de resposta.
                    new ResponseMercado(
                        // Buscando o mercado pelo ID.
                        mercadoRepository.findById(
                            // Passando como parâmetro o ID do mercado no estoque atual.
                            estoque.getMercadoId()
                        )
                        .get()
                    )
                );
            }

            // Se não houver nenhum mercado que possua o produto
            if(responseMercado.isEmpty())
                // Lança uma exceção informando que não foi encontrado um estoque com o produto informado.
                throw new NoSuchElementException("estoque_nao_encontrado");
            
            // Faz a ordenação da lista de sugestões.
            responseMercado.sort((o1, o2) -> {

                switch (ordenarPor) {

                    case "id":

                        // Se a ordem for "asc"
                        if(ordem.equals("asc"))
                            // Ordena a lista de produtos por id ascendente.
                            return o1.getId().compareTo(o2.getId());
                        // Se a ordem for "desc"
                        else
                            // Ordena a lista de produtos por id descendente.
                            return o2.getId().compareTo(o1.getId());
                    
                    case "nome":

                        // Se a ordem for "asc"
                        if(ordem.equals("asc"))
                            // Ordena a lista de produtos por nome ascendente.
                            return o1.getNome().compareTo(o2.getNome());
                        // Se a ordem for "desc"
                        else
                            // Ordena a lista de produtos por nome descendente.
                            return o2.getNome().compareTo(o1.getNome());
                    
                    case "logradouro":

                        // Se a ordem for "asc"
                        if(ordem.equals("asc"))
                            // Ordena a lista de produtos por logradouro ascendente.
                            return o1.getLogradouro().compareTo(o2.getLogradouro());
                        // Se a ordem for "desc"
                        else
                            // Ordena a lista de produtos por logradouro descendente.
                            return o2.getLogradouro().compareTo(o1.getLogradouro());
                    
                    case "numero":

                        // Se a ordem for "asc"
                        if(ordem.equals("asc"))
                            // Ordena a lista de produtos por numero ascendente.
                            return o1.getNumero().compareTo(o2.getNumero());
                        // Se a ordem for "desc"
                        else
                            // Ordena a lista de produtos por numero descendente.
                            return o2.getNumero().compareTo(o1.getNumero());
                    
                    case "complemento":

                        // Se a ordem for "asc"
                        if(ordem.equals("asc"))
                            // Ordena a lista de produtos por complemento ascendente.
                            return o1.getComplemento().compareTo(o2.getComplemento());
                        // Se a ordem for "desc"
                        else
                            // Ordena a lista de produtos por complemento descendente.
                            return o2.getComplemento().compareTo(o1.getComplemento());
                    
                    case "bairro":

                        // Se a ordem for "asc"
                        if(ordem.equals("asc"))
                            // Ordena a lista de produtos por bairro ascendente.
                            return o1.getBairro().compareTo(o2.getBairro());
                        // Se a ordem for "desc"
                        else
                            // Ordena a lista de produtos por bairro descendente.
                            return o2.getBairro().compareTo(o1.getBairro());
                    
                    case "cidade":

                        // Se a ordem for "asc"
                        if(ordem.equals("asc"))
                            // Ordena a lista de produtos por cidade ascendente.
                            return o1.getCidade().compareTo(o2.getCidade());
                        // Se a ordem for "desc"
                        else
                            // Ordena a lista de produtos por cidade descendente.
                            return o2.getCidade().compareTo(o1.getCidade());
                    
                    case "uf":

                        // Se a ordem for "asc"
                        if(ordem.equals("asc"))
                            // Ordena a lista de produtos por uf ascendente.
                            return o1.getUf().compareTo(o2.getUf());
                        // Se a ordem for "desc"
                        else
                            // Ordena a lista de produtos por uf descendente.
                            return o2.getUf().compareTo(o1.getUf());
                    
                    case "cep":

                        // Se a ordem for "asc"
                        if(ordem.equals("asc"))
                            // Ordena a lista de produtos por cep ascendente.
                            return o1.getCep().compareTo(o2.getCep());
                        // Se a ordem for "desc"
                        else
                            // Ordena a lista de produtos por cep descendente.
                            return o2.getCep().compareTo(o1.getCep());
                    
                    case "ramoId":

                        // Se a ordem for "asc"
                        if(ordem.equals("asc"))
                            // Ordena a lista de produtos por ramoId ascendente.
                            return o1.getRamoId().compareTo(o2.getRamoId());
                        // Se a ordem for "desc"
                        else
                            // Ordena a lista de produtos por ramoId descendente.
                            return o2.getRamoId().compareTo(o1.getRamoId());
                
                    default:
                        return 0;
                }
            });

            // Para cada mercado da lista de resposta.
            for(ResponseMercado mercado : responseMercado) {
                // Adiciona ao mercado o link para a leitura do mesmo.
                mercado.add(
                    linkTo(
                        methodOn(MercadoController.class).ler(mercado.getId())
                    )
                    .withSelfRel()
                );                

                // Adiciona ao mercado o link para a rota de listagem de mercados (coleção).
                mercado.add(
                    linkTo(
                        methodOn(MercadoController.class).listar(new Mercado())
                    )
                    .withRel("collection")
                );
            }

            // Retorna os dados do produto.
            return responseMercado;

        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Sobrecarga do método responsável por listar todos os mercados onde o produto pode ser encontrado. Pode ser ordenado e paginado.
     * @param id - Id do produto a ser buscado como parâmetro.
     * @param ordenarPor - Campo do banco de dados que servirá de parâmetro para ordenar.
     * @param ordem - Direção em que os dados serão ordenados entre "asc" e "desc".
     * @param pagina - Número da página que será listada.
     * @param limite - Número de registros que serão listados por página.
     * @return <b>ResponsePagina</b> - Objeto com as informações da página e a lista de mercados onde o produto pode ser encontrado ordenada.
     */
    @GetMapping(value = "/{id}/mercado", params = {"ordenarPor", "ordem", "pagina", "limite"})
    @Cacheable("produtoMercados")
    public ResponsePagina listarMercados(
        @PathVariable("id") Integer id,
        @RequestParam(required = false, defaultValue = "") String ordenarPor,
        @RequestParam(required = false, defaultValue = "asc") String ordem,
        @RequestParam(required = false, defaultValue = "0") Integer pagina,
        @RequestParam(required = false, defaultValue = "10") Integer limite
    ) {
        try {

            // Buscando os estoques que possuem o produto informado e armazenando numa lista.
            List<Estoque> estoques = estoqueRepository.findByProdutoId(id);

            // Busca o produto no banco e transforma os dados obtidos em modelo de resposta.
            List<ResponseMercado> mercados = new ArrayList<>();

            // Percorrendo cada estoque encontrado
            for(Estoque estoque : estoques){
                // Armazenando o mercado do estoque na lista de resposta com os mercados encontrados.
                mercados.add(
                    // Chamando o construtor da classe ResponseMercado que converte o objeto Mercado em modelo de resposta.
                    new ResponseMercado(
                        // Buscando o mercado pelo ID.
                        mercadoRepository.findById(
                            // Passando como parâmetro o ID do mercado no estoque atual.
                            estoque.getMercadoId()
                        )
                        .get()
                    )
                );
            }

            // Se não houver nenhum mercado que possua o produto
            if(mercados.isEmpty())
                // Lança uma exceção informando que não foi encontrado um estoque com o produto informado.
                throw new NoSuchElementException("estoque_nao_encontrado");
            
            // Faz a ordenação da lista de sugestões.
            mercados.sort((o1, o2) -> {

                switch (ordenarPor) {

                    case "id":

                        // Se a ordem for "asc"
                        if(ordem.equals("asc"))
                            // Ordena a lista de produtos por id ascendente.
                            return o1.getId().compareTo(o2.getId());
                        // Se a ordem for "desc"
                        else
                            // Ordena a lista de produtos por id descendente.
                            return o2.getId().compareTo(o1.getId());
                    
                    case "nome":

                        // Se a ordem for "asc"
                        if(ordem.equals("asc"))
                            // Ordena a lista de produtos por nome ascendente.
                            return o1.getNome().compareTo(o2.getNome());
                        // Se a ordem for "desc"
                        else
                            // Ordena a lista de produtos por nome descendente.
                            return o2.getNome().compareTo(o1.getNome());
                    
                    case "logradouro":

                        // Se a ordem for "asc"
                        if(ordem.equals("asc"))
                            // Ordena a lista de produtos por logradouro ascendente.
                            return o1.getLogradouro().compareTo(o2.getLogradouro());
                        // Se a ordem for "desc"
                        else
                            // Ordena a lista de produtos por logradouro descendente.
                            return o2.getLogradouro().compareTo(o1.getLogradouro());
                    
                    case "numero":

                        // Se a ordem for "asc"
                        if(ordem.equals("asc"))
                            // Ordena a lista de produtos por numero ascendente.
                            return o1.getNumero().compareTo(o2.getNumero());
                        // Se a ordem for "desc"
                        else
                            // Ordena a lista de produtos por numero descendente.
                            return o2.getNumero().compareTo(o1.getNumero());
                    
                    case "complemento":

                        // Se a ordem for "asc"
                        if(ordem.equals("asc"))
                            // Ordena a lista de produtos por complemento ascendente.
                            return o1.getComplemento().compareTo(o2.getComplemento());
                        // Se a ordem for "desc"
                        else
                            // Ordena a lista de produtos por complemento descendente.
                            return o2.getComplemento().compareTo(o1.getComplemento());
                    
                    case "bairro":

                        // Se a ordem for "asc"
                        if(ordem.equals("asc"))
                            // Ordena a lista de produtos por bairro ascendente.
                            return o1.getBairro().compareTo(o2.getBairro());
                        // Se a ordem for "desc"
                        else
                            // Ordena a lista de produtos por bairro descendente.
                            return o2.getBairro().compareTo(o1.getBairro());
                    
                    case "cidade":

                        // Se a ordem for "asc"
                        if(ordem.equals("asc"))
                            // Ordena a lista de produtos por cidade ascendente.
                            return o1.getCidade().compareTo(o2.getCidade());
                        // Se a ordem for "desc"
                        else
                            // Ordena a lista de produtos por cidade descendente.
                            return o2.getCidade().compareTo(o1.getCidade());
                    
                    case "uf":

                        // Se a ordem for "asc"
                        if(ordem.equals("asc"))
                            // Ordena a lista de produtos por uf ascendente.
                            return o1.getUf().compareTo(o2.getUf());
                        // Se a ordem for "desc"
                        else
                            // Ordena a lista de produtos por uf descendente.
                            return o2.getUf().compareTo(o1.getUf());
                    
                    case "cep":

                        // Se a ordem for "asc"
                        if(ordem.equals("asc"))
                            // Ordena a lista de produtos por cep ascendente.
                            return o1.getCep().compareTo(o2.getCep());
                        // Se a ordem for "desc"
                        else
                            // Ordena a lista de produtos por cep descendente.
                            return o2.getCep().compareTo(o1.getCep());
                    
                    case "ramoId":

                        // Se a ordem for "asc"
                        if(ordem.equals("asc"))
                            // Ordena a lista de produtos por ramoId ascendente.
                            return o1.getRamoId().compareTo(o2.getRamoId());
                        // Se a ordem for "desc"
                        else
                            // Ordena a lista de produtos por ramoId descendente.
                            return o2.getRamoId().compareTo(o1.getRamoId());
                
                    default:
                        return 0;
                }
            });

            // Mercados que serão mostradas na página atual.
            // O início da página é a multiplicação do número da página pelo limite de mercados por página.
            // O fim da página é o inicío da página mais o limite de mercados por página.
            List<ResponseMercado> mercadosPagina = mercados.subList(pagina * limite, (pagina * limite) + limite);

            // Representa o total de páginas da pesquisa.
            // O total de páginas é calculado dividindo o total de mercados pelo limite de mercados por página.
            // O valor é arredondado para cima porque existe a possibilidade da ultima página não possuir o número limite de mercados.
            Integer totalPaginas = (int) Math.ceil(mercados.size() / (double) limite);

            // Total de mercados da pesquisa.
            // Se o tamanho for maior que Integer.MAX_VALUE, o valor é Integer.MAX_VALUE.
            Integer totalRegistros = mercados.size();

            // Para cada mercado da lista de resposta.
            for(ResponseMercado mercado : mercados) {
                // Adiciona ao mercado o link para a leitura do mesmo.
                mercado.add(
                    linkTo(
                        methodOn(MercadoController.class).ler(mercado.getId())
                    )
                    .withSelfRel()
                );                

                // Adiciona ao mercado o link para a rota de listagem de mercados (coleção).
                mercado.add(
                    linkTo(
                        methodOn(MercadoController.class).listar(new Mercado())
                    )
                    .withRel("collection")
                );
            }

            // Prepara uma resposta em formato de página
            ResponsePagina responseMercado = PaginaUtils.criarResposta(pagina, limite, totalRegistros, totalPaginas, mercadosPagina);

            // Adiciona à resposta um link para a primeira página da listagem de mercados.
            responseMercado.add(
                linkTo(
                    methodOn(ProdutoController.class).listarMercados(id, 0, limite)
                )
                .withRel("first")
            );

            // Se a página de mercados não estiver vazia.
            if(!mercados.isEmpty()) {
                // Se a página informada pelo cliente não for a primeira página da listagem de mercados.
                if(pagina > 0) {
                    // Adiciona à resposta um link para a página anterior da listagem de mercados.
                    responseMercado.add(
                        linkTo(
                            methodOn(ProdutoController.class).listarMercados(id, pagina - 1, limite)
                        )
                        .withRel("previous")
                    );
                }

                // Se a página informada pelo cliente não for a última página da listagem de mercados.
                if(pagina < totalPaginas - 1) {
                    // Adiciona à resposta um link para a página seguinte da listagem de mercados.
                    responseMercado.add(
                        linkTo(
                            methodOn(ProdutoController.class).listarMercados(id, pagina + 1, limite)
                        )
                        .withRel("next")
                    );
                }

                // Adiciona à resposta um link para a última página da listagem de mercados.
                responseMercado.add(
                    linkTo(
                        methodOn(ProdutoController.class).listarMercados(id, totalPaginas - 1, limite)
                    )
                    .withRel("last")
                );
            }

            // Retorna os dados do produto.
            return responseMercado;

        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Rota responsável pelo levanamento de preços de um produto.
     * @param id - Id do produto.
     * @return Informações do levantamento e lista de preços.
     */
    @GetMapping("/{id}/levantamento")
    @Cacheable("produtoLevantamento")
    public ResponseLevantamentoProduto levantamento(@PathVariable("id") Integer id) {
        try {

            // Busca o produto no banco e transforma os dados obtidos em modelo de resposta.
            ResponseLevantamentoProduto responseProduto = new ResponseLevantamentoProduto(produtoRepository.findById(id).get());

            // Busca os estoques do produto.
            List<Estoque> estoques = estoqueRepository.findByProdutoId(id);

            float somaPreco = 0.0f; // Soma do preço encontrado em cada mercado.
            int quantidadeSugestoes = 0; // Quantidade total de sugestões encontradas.

            // Se houver estoques desse produto,
            if(estoques != null) {
                // Percorre os estoques do produto.
                for(Estoque estoque : estoques) {
                    // Buscando as sugestões do produto no mercado atual
                    List<Sugestao> sugestoes = sugestaoRepository.findByEstoqueId(estoque.getId());

                    // Se houver sugestões do produto no mercado atual,
                    if(sugestoes != null) {
                        // Percorre as sugestões do produto,
                        for(Sugestao sugestao : sugestoes) {
                            // Incrementa a quantidade de sugestões encontradas.
                            quantidadeSugestoes++;
                            // Soma o preço da sugestão.
                            somaPreco += (sugestao.getPreco() / 100);

                            // Se não houver data da ultima sugestão,
                            if(responseProduto.getDataUltimaSugestao() == null)
                                // Define a data do momento
                                responseProduto.setDataUltimaSugestao(sugestao.getTimestamp());
                            // Ou se a data da ultima sugestão for antes da data do momento,
                            else if(responseProduto.getDataUltimaSugestao().before(sugestao.getTimestamp()))
                                // Define a data do momento
                                responseProduto.setDataUltimaSugestao(sugestao.getTimestamp());

                            // Se o maior preço encontrado for 0,
                            if(responseProduto.getMaiorPreco() == 0.0f)
                                // Define o maior preço encontrado como o preço da sugestão atual
                                responseProduto.setMaiorPreco(sugestao.getPreco() / 100);
                            // Ou se o preço da sugestão atual for maior que o maior preço encontrado,
                            else if(responseProduto.getMaiorPreco() < sugestao.getPreco())
                                // Define o maior preço encontrado como o preço da sugestão atual
                                responseProduto.setMaiorPreco(sugestao.getPreco() / 100);

                            // Se o menor preço encontrado for 0,
                            if(responseProduto.getMenorPreco() == 0.0f)
                                // Define o menor preço encontrado como o preço da sugestão atual
                                responseProduto.setMenorPreco(sugestao.getPreco() / 100);
                            // Ou se o preço da sugestão atual for maior que o menor preço encontrado,
                            else if(responseProduto.getMenorPreco() > sugestao.getPreco())
                                // Define o menor preço encontrado como o preço da sugestão atual
                                responseProduto.setMenorPreco(sugestao.getPreco() / 100);
                        }
                    }
                }
            }

            // Define a quantidade de sugestões encontradas.
            responseProduto.setQuantidadeSugestoes(quantidadeSugestoes);
            // Define o preço médio do produto.
            responseProduto.setPrecoMedio(quantidadeSugestoes > 0 ? somaPreco / quantidadeSugestoes : 0.0f);

            // Adiciona o link para a rota de listagem de produtos.
            if(responseProduto != null) {
                responseProduto.add(
                    linkTo(
                        methodOn(ProdutoController.class).listar(new Produto())
                    )
                    .withRel("collection")
                );
            }

            return responseProduto;

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(500, "erro_interno", e);
        } catch (NullPointerException e) {
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (UnsupportedOperationException e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Rota responsável por listar todos os produtos.
     * @param requestProduto - Dados de pesquisa para filtragem.
     * @return Lista de produtos.
     */
    @GetMapping
    @Cacheable("Produtos")
    public List<ResponseProduto> listar(Produto requestProduto) {
        try {

            // Valida os dados de pesquisa.
            Tratamento.validarProduto(requestProduto, true);

            // Busca os produtos no banco semelhantes aos dados de pesquisa.
            // Se não houver dados de pesquisa, busca todos os produtos.
            List<Produto> produtos = produtoRepository.findAll(
                Example.of(
                    requestProduto, 
                    ExampleMatcher
                        .matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                )
            );

            // Lista de produtos que será retornada.
            List<ResponseProduto> responseProduto = new ArrayList<ResponseProduto>();

            // Adiciona os prdutos à lista.
            for(Produto produto : produtos) {
                responseProduto.add(new ResponseProduto(produto));
            }

            // Se houver produtos,
            if(responseProduto != null) {
                // Percorre os produtos,
                for(ResponseProduto produto : responseProduto) {
                    // Adiciona o link para a rota de detalhamento de produto.
                    produto.add(
                        linkTo(
                            methodOn(ProdutoController.class).ler(produto.getId())
                        )
                        .withSelfRel()
                    );
                }
            }

            return responseProduto;

        } catch (DadosInvalidosException e) {
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (NullPointerException e) {
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (UnsupportedOperationException e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Rota responsável por listar produtos.
     * @param requestProduto - Dados de pesquisa para filtragem.
     * @param pagina - Número da página a ser mostrada.
     * @param limite - Número de registros por página.
     * @return Lista de produtos com os dados da página.
     */
    @GetMapping(params = { "pagina", "limite" })
    @Cacheable("Produtos")
    public ResponsePagina listar(
        Produto requestProduto,
        @RequestParam(required = false, defaultValue = "0") Integer pagina,
        @RequestParam(required = false, defaultValue = "10") Integer limite
    ) {
        try {

            // Valida os dados de pesquisa.
            Tratamento.validarProduto(requestProduto, true);

            // Busca os produtos no banco semelhantes aos dados de pesquisa.
            // Se não houver dados de pesquisa, busca todos os produtos.
            // Informa os dados de paginação.
            // Se não houver paginação, busca todos os produtos.
            Page<Produto> paginaProduto = produtoRepository.findAll(
                Example.of(
                    requestProduto, 
                    ExampleMatcher
                        .matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                ),
                PageRequest.of(pagina, limite)
            );

            // Lista de produtos que será retornada.
            List<ResponseProduto> produtos = new ArrayList<ResponseProduto>();

            // Formata a resposta com os dados obtidos.
            ResponsePagina responseProduto = PaginaUtils.criarResposta(pagina, limite, paginaProduto, produtos);

            // Adiciona os prdutos à lista.
            for(Produto produto : paginaProduto.getContent()) {
                produtos.add(new ResponseProduto(produto));
            }

            // Adiciona um link para a primeira página de produtos.
            responseProduto.add(
                linkTo(
                    methodOn(ProdutoController.class).listar(requestProduto, 0, limite)
                )
                .withRel("first")
            );

            // Se houver produtos,
            if(!paginaProduto.isEmpty()) {
                // Se a página atual não for a primeira,
                if(pagina > 0) {
                    // Adiciona um link para a página anterior de produtos.
                    responseProduto.add(
                        linkTo(
                            methodOn(ProdutoController.class).listar(requestProduto, pagina - 1, limite)
                        )
                        .withRel("previous")
                    );
                }
                
                // Se a página atual não for a última,
                if(pagina < paginaProduto.getTotalPages() - 1) {
                    // Adiciona um link para a página seguinte de produtos.
                    responseProduto.add(
                        linkTo(
                            methodOn(ProdutoController.class).listar(requestProduto, pagina + 1, limite)
                        )
                        .withRel("next")
                    );
                }

                // Adiciona um link para a última página de produtos.
                responseProduto.add(
                    linkTo(
                        methodOn(ProdutoController.class).listar(requestProduto, paginaProduto.getTotalPages() - 1, limite)
                    )
                    .withRel("last")
                );

                // Percorre os produtos,
                for(ResponseProduto produto : produtos) {
                    // Adiciona o link para a rota de detalhamento de produto.
                    produto.add(
                        linkTo(
                            methodOn(ProdutoController.class).ler(produto.getId())
                        )
                        .withSelfRel()
                    );
                }
            }

            // Retorna a resposta.
            return responseProduto;

        } catch (DadosInvalidosException e) {
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (NullPointerException e) {
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Sobrecarga do método responsável por listar produtos. Pode ser ordenado.
     * @param requestProduto - Dados de pesquisa para filtragem.
     * @param ordenarPor - Campo do banco de dados que servirá de parâmetro para ordenar.
     * @param ordem - Direção em que os dados serão ordenados entre "asc" e "desc".
     * @return Lista de produtos ordenados.
     */
    @GetMapping(params = { "ordenarPor", "ordem" })
    @Cacheable("Produtos")
    public List<ResponseProduto> listar(
        Produto requestProduto,
        @RequestParam(required = false, defaultValue = "") String ordenarPor,
        @RequestParam(required = false, defaultValue = "asc") String ordem
    ) {
        try {

            // Valida os dados de pesquisa.
            Tratamento.validarProduto(requestProduto, true);

            // Busca os produtos no banco semelhantes aos dados de pesquisa.
            // Se não houver dados de pesquisa, busca todos os produtos.
            List<Produto> produtos = produtoRepository.findAll(
                Example.of(
                    requestProduto, 
                    ExampleMatcher
                        .matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                ),
                Sort.by(Sort.Direction.fromString(ordem), ordenarPor)
            );

            // Lista de produtos que será retornada.
            List<ResponseProduto> responseProduto = new ArrayList<ResponseProduto>();

            // Adiciona os prdutos à lista.
            for(Produto produto : produtos) {
                responseProduto.add(new ResponseProduto(produto));
            }

            // Se houver produtos,
            if(responseProduto != null) {
                // Percorre os produtos,
                for(ResponseProduto produto : responseProduto) {
                    // Adiciona o link para a rota de detalhamento de produto.
                    produto.add(
                        linkTo(
                            methodOn(ProdutoController.class).ler(produto.getId())
                        )
                        .withSelfRel()
                    );
                }
            }

            return responseProduto;

        } catch (DadosInvalidosException e) {
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (NullPointerException e) {
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (UnsupportedOperationException e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Sobrecarga do método responsável por listar produtos. Pode ser ordenado e paginado.
     * @param requestProduto - Dados de pesquisa para filtragem.
     * @param ordenarPor - Campo do banco de dados que servirá de parâmetro para ordenar.
     * @param ordem - Direção em que os dados serão ordenados entre "asc" e "desc".
     * @param pagina - Número da página a ser mostrada.
     * @param limite - Número de registros por página.
     * @return Lista de produtos ordenados com os dados da página.
     */
    @GetMapping(params = { "ordenarPor", "ordem", "pagina", "limite" })
    @Cacheable("Produtos")
    public ResponsePagina listar(
        Produto requestProduto,
        @RequestParam(required = false, defaultValue = "") String ordenarPor,
        @RequestParam(required = false, defaultValue = "asc") String ordem,
        @RequestParam(required = false, defaultValue = "0") Integer pagina,
        @RequestParam(required = false, defaultValue = "10") Integer limite
    ) {
        try {

            // Valida os dados de pesquisa.
            Tratamento.validarProduto(requestProduto, true);

            // Busca os produtos no banco semelhantes aos dados de pesquisa.
            // Se não houver dados de pesquisa, busca todos os produtos.
            // Informa os dados de paginação.
            // Se não houver paginação, busca todos os produtos.
            Page<Produto> paginaProduto = produtoRepository.findAll(
                Example.of(
                    requestProduto, 
                    ExampleMatcher
                        .matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                ),
                PageRequest.of(pagina, limite, Sort.by(Sort.Direction.fromString(ordem), ordenarPor))
            );

            // Lista de produtos que será retornada.
            List<ResponseProduto> produtos = new ArrayList<ResponseProduto>();

            // Formata a resposta com os dados obtidos.
            ResponsePagina responseProduto = PaginaUtils.criarResposta(pagina, limite, paginaProduto, produtos);

            // Adiciona os prdutos à lista.
            for(Produto produto : paginaProduto.getContent()) {
                produtos.add(new ResponseProduto(produto));
            }

            // Adiciona um link para a primeira página de produtos.
            responseProduto.add(
                linkTo(
                    methodOn(ProdutoController.class).listar(requestProduto, 0, limite)
                )
                .withRel("first")
            );

            // Se houver produtos,
            if(!paginaProduto.isEmpty()) {
                // Se a página atual não for a primeira,
                if(pagina > 0) {
                    // Adiciona um link para a página anterior de produtos.
                    responseProduto.add(
                        linkTo(
                            methodOn(ProdutoController.class).listar(requestProduto, pagina - 1, limite)
                        )
                        .withRel("previous")
                    );
                }
                
                // Se a página atual não for a última,
                if(pagina < paginaProduto.getTotalPages() - 1) {
                    // Adiciona um link para a página seguinte de produtos.
                    responseProduto.add(
                        linkTo(
                            methodOn(ProdutoController.class).listar(requestProduto, pagina + 1, limite)
                        )
                        .withRel("next")
                    );
                }

                // Adiciona um link para a última página de produtos.
                responseProduto.add(
                    linkTo(
                        methodOn(ProdutoController.class).listar(requestProduto, paginaProduto.getTotalPages() - 1, limite)
                    )
                    .withRel("last")
                );

                // Percorre os produtos,
                for(ResponseProduto produto : produtos) {
                    // Adiciona o link para a rota de detalhamento de produto.
                    produto.add(
                        linkTo(
                            methodOn(ProdutoController.class).ler(produto.getId())
                        )
                        .withSelfRel()
                    );
                }
            }

            // Retorna a resposta.
            return responseProduto;

        } catch (DadosInvalidosException e) {
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (NullPointerException e) {
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Rota responsável por editar um produto.
     * @param id - Id do produto a ser editado.
     * @param requestProduto - Dados modificados do produto.
     * @return Dados novos do produto editado.
     */
    @PatchMapping("/{id}")
    public ResponseProduto editar(@PathVariable int id, @RequestBody Produto requestProduto) {
        try {

            // Valida os dados fornecidos.
            Tratamento.validarProduto(requestProduto, true);

            // Se as características fornecidas pertencer a um produto já existente,
            if(produtoRepository.findByCaracteristicas(
                requestProduto.getNome(), 
                requestProduto.getMarca(),
                requestProduto.getTamanho(), 
                requestProduto.getCor()) != null
            )
                // Retorna um erro.
                throw new DadosConflitantesException("produto_existente");

            // Busca o produto a ser editado.
            Produto produtoAtual = produtoRepository.findById(id).get();
            
            // Se a categoria informada não existir,
            if(!categoriaRepository.existsById(requestProduto.getCategoriaId()))
                // Retorna um erro.
                throw new DadosInvalidosException("categoria_nao_encontrado");

            // Chama o recurso de tratamento de edição de produto.
            // Insere os dados tratados no banco de dados.
            // Transforma os dados inseridos em resposta.
            ResponseProduto responseProduto = new ResponseProduto(
                produtoRepository.save(
                    EditaRecurso.editarProduto(
                        produtoAtual, 
                        requestProduto
                    )
                )
            );
    
            // Adiciona o link para a rota de detalhamento de produto.
            responseProduto.add(
                linkTo(
                    methodOn(ProdutoController.class).ler(responseProduto.getId())
                )
                .withSelfRel()
            );

            // Retorna a resposta.
            return responseProduto;

        } catch (DadosConflitantesException e) {
            throw new ResponseStatusException(409, e.getMessage(), e);
        } catch (DadosInvalidosException e) {
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(500, "erro_insercao", e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Rota responsável por substituir um produto.
     * @param id - Id do produto a ser substituído.
     * @param requestProduto - Dados do novo produto.
     * @return Dados do novo produto.
     */
    @PutMapping("/{id}")
    public ResponseProduto atualizar(@PathVariable int id, @RequestBody Produto requestProduto) {
        try {

            // Valida os dados fornecidos.
            Tratamento.validarProduto(requestProduto, false);

            // Se o usuário informado não existir,
            if(!usuarioRepository.existsById(requestProduto.getCriadoPor()))
                // Retorna um erro.
                throw new DadosInvalidosException("usuario_nao_encontrado");

            // Se a categoria informada não existir,
            if(!categoriaRepository.existsById(requestProduto.getCategoriaId()))
                // Retorna um erro.
                throw new DadosInvalidosException("categoria_nao_encontrado");

            // Se as características fornecidas pertencer a um produto já existente,
            if(produtoRepository.findByCaracteristicas(
                requestProduto.getNome(), 
                requestProduto.getMarca(),
                requestProduto.getTamanho(), 
                requestProduto.getCor()) != null
            )
                // Retorna um erro.
                throw new DadosConflitantesException("produto_existente");

            // Busca o produto a ser substituído.
            Produto produto = produtoRepository.findById(id).get();

            // Define o id do produto como o id fornecido.
            requestProduto.setId(id);

            // Define o id do usuário criador como o id atual do banco dedos, haja vista que não pode ser alterado.
            requestProduto.setCriadoPor(produto.getCriadoPor());
            
            // Insere o novo produto e transforma os dados inseridos em resposta.
            ResponseProduto responseProduto = new ResponseProduto(produtoRepository.save(requestProduto));

            // Adiciona o link para a rota de detalhamento de produto.
            responseProduto.add(
                linkTo(
                    methodOn(ProdutoController.class).ler(responseProduto.getId())
                )
                .withSelfRel()
            );

            // Retorna a resposta.
            return responseProduto;

        } catch (DadosConflitantesException e) {
            throw new ResponseStatusException(409, e.getMessage(), e);
        } catch (DadosInvalidosException e) {
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(404, e.getMessage(), e);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(500, "erro_insercao", e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Rota responsável por excluir um produto.
     * @param id - Id do produto a ser excluído.
     */
    @DeleteMapping("/{id}")
    @CacheEvict(value = "Produtos", allEntries = true)
    public Object remover(@PathVariable int id) {
        try {

            // Se o produto informado não existir,
            if(!produtoRepository.existsById(id))
                // Retorna um erro.
                throw new NoSuchElementException("nao_encontrado");

            // Exclui o produto.
            produtoRepository.deleteById(id);

            // Retorna um link para a rota de listagem de produtos.
            return linkTo(
                        methodOn(ProdutoController.class).listar(new Produto())
                    )
                    .withRel("collection");

        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(404, e.getMessage(), e);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(500, "erro_remocao", e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }
}
