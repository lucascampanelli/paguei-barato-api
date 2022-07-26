package com.pagueibaratoapi.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.pagueibaratoapi.models.exceptions.DadosConflitantesException;
import com.pagueibaratoapi.models.exceptions.DadosInvalidosException;
import com.pagueibaratoapi.models.requests.Estoque;
import com.pagueibaratoapi.models.requests.Mercado;
import com.pagueibaratoapi.models.requests.Produto;
import com.pagueibaratoapi.models.requests.Sugestao;
import com.pagueibaratoapi.models.requests.Usuario;
import com.pagueibaratoapi.models.responses.ResponseEstoque;
import com.pagueibaratoapi.models.responses.ResponseEstoqueProduto;
import com.pagueibaratoapi.models.responses.ResponseMercado;
import com.pagueibaratoapi.models.responses.ResponsePagina;
import com.pagueibaratoapi.models.responses.ResponseProduto;
import com.pagueibaratoapi.models.responses.ResponseSugestao;
import com.pagueibaratoapi.repository.CategoriaRepository;
import com.pagueibaratoapi.repository.EstoqueRepository;
import com.pagueibaratoapi.repository.MercadoRepository;
import com.pagueibaratoapi.repository.ProdutoRepository;
import com.pagueibaratoapi.repository.RamoRepository;
import com.pagueibaratoapi.repository.SugestaoRepository;
import com.pagueibaratoapi.repository.UsuarioRepository;
import com.pagueibaratoapi.utils.EditaRecurso;
import com.pagueibaratoapi.utils.PaginaUtils;
import com.pagueibaratoapi.utils.Tratamento;

/**
* Classe responsável por controlar as requisições de Mercado.
*/
@RestController
@RequestMapping("/mercado")
public class MercadoController {

    // Iniciando as variáveis de instância dos repositórios.
    private final CategoriaRepository categoriaRepository;
    private final EstoqueRepository estoqueRepository;
    private final MercadoRepository mercadoRepository;
    private final ProdutoRepository produtoRepository;
    private final RamoRepository ramoRepository;
    private final SugestaoRepository sugestaoRepository;
    private final UsuarioRepository usuarioRepository;

    // Construtor do controller do estoque, que realiza a injeção de dependência dos repositórios.
    public MercadoController(
        CategoriaRepository categoriaRepository,
        EstoqueRepository estoqueRepository,
        MercadoRepository mercadoRepository,
        ProdutoRepository produtoRepository,
        RamoRepository ramoRepository,
        SugestaoRepository sugestaoRepository,
        UsuarioRepository usuarioRepository
    ) {
        this.categoriaRepository = categoriaRepository;
        this.estoqueRepository = estoqueRepository;
        this.mercadoRepository = mercadoRepository;
        this.produtoRepository = produtoRepository;
        this.ramoRepository = ramoRepository;
        this.sugestaoRepository = sugestaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Método responsável por criar um novo mercado.
     * @param requestMercado - Objeto do tipo Mercado que contém os dados do novo mercado.
     * @return ResponseMercado - Objeto do tipo ResponseMercado que contém os dados do novo mercado, já com o Id criado.
     */
    @PostMapping
    public ResponseMercado criar(@RequestBody Mercado requestMercado) {
        try {

            // Validando os dados do mercado enviados pelo cliente.
            Tratamento.validarMercado(requestMercado, false);

            // Verificando se o usuário informado existe.
            if(!usuarioRepository.existsById(requestMercado.getCriadoPor()))
                // Se não existir, lança exceção com mensagem de erro.
                throw new DadosInvalidosException("usuario_nao_encontrado");

            Usuario usuario = usuarioRepository.findById(requestMercado.getCriadoPor()).get();

            // Verificando se o usuário informado não foi deletado.
            if(!Tratamento.usuarioExiste(usuario))
                // Se o usuário foi deletado, lança exceção com mensagem de erro.
                throw new NoSuchElementException("usuario_nao_encontrado");

            // Verificando se o ramo informado existe.
            if(!ramoRepository.existsById(requestMercado.getRamoId()))
                // Se não existirm, lança exceção com mensagem de erro.
                throw new DadosInvalidosException("ramo_nao_encontrado");

            // Verificando se o nome do mercado informado já existe
            if(mercadoRepository.existsByNomeIgnoreCase(requestMercado.getNome()))
                throw new DadosConflitantesException("mercado_existente");

            // Verificando se o endereço do mercado informado já existe
            if(mercadoRepository.findByEndereco(
                requestMercado.getLogradouro(), 
                requestMercado.getNumero(),
                requestMercado.getComplemento(), 
                requestMercado.getBairro(), 
                requestMercado.getCidade(),
                requestMercado.getUf(), 
                requestMercado.getCep()) != null
            )
                // Se já existir, lança exceção com mensagem de erro, 
                // haja vista que não pode haver dois mercados no mesmo local.
                throw new DadosConflitantesException("mercado_existente");

            // Cria o mercado e armazena o retorno num objeto do tipo ResponseMercado.
            ResponseMercado responseMercado = new ResponseMercado(mercadoRepository.save(requestMercado));

            // Adiciona ao retorno um link para ler o mercado criado
            responseMercado.add(
                linkTo(
                    methodOn(MercadoController.class).ler(responseMercado.getId())
                )
                .withSelfRel()
            );

            // Retorna o objeto do tipo ResponseMercado com o mercado criado e o link para sua leitura.
            return responseMercado;

        } catch (DadosConflitantesException e) {
            // Lança uma exceção informando que os dados estão em conflito com outros dados no banco de dados.
            throw new ResponseStatusException(409, e.getMessage(), e);
        } catch(NoSuchElementException e) {
            // Lança uma exceção informando que algum registro informado não existe.
            throw new ResponseStatusException(404, e.getMessage(), e);
        } catch (DadosInvalidosException e) {
            // Lança uma exceção informando que os dados enviados pelo cliente são inválidos.
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (DataIntegrityViolationException e) {
            // Lança uma exceção informando que os dados enviados pelo cliente são inválidos.
            throw new ResponseStatusException(500, "erro_insercao", e);
        } catch (IllegalArgumentException e) {
            // Lança uma exceção informando que os dados enviados pelo cliente são inválidos.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (Exception e) {
            // Lança uma exceção informando que ocorreu um erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Método responsável por adicionar um produto ao mercado com o id informado.
     * @param id - Id do mercado que será usado como parâmetro.
     * @param produtoId - Id do produto no qual será buscadas as sugestões.
     * @return ResponseEstoque - Objeto do estoque criado.
     */
    @PostMapping("/{id}/produto/{produtoId}")
    public ResponseEstoque criarEstoque(
        @PathVariable("id") Integer id,
        @PathVariable(value = "produtoId") Integer produtoId,
        @RequestBody Estoque requestEstoque
    ) {
        try {

            // Validando os dados enviados pelo usuário.
            Tratamento.validarEstoque(requestEstoque, true);

            // Se o cliente não enviou o id do usuário criador do estoque
            if(requestEstoque.getCriadoPor() == null)
                // Lança um erro de usuário não encontrado.
                throw new DadosInvalidosException("usuario_nao_encontrado");

            // Verifica se o usuário informado existe. Caso não exista, lança exceção.
            if(!usuarioRepository.existsById(requestEstoque.getCriadoPor()))
                throw new DadosInvalidosException("usuario_invalido");

            // Obtendo o usuário informado.
            Usuario usuario = usuarioRepository.findById(requestEstoque.getCriadoPor()).get();

            // Verifica se o usuário informado como criador não foi deletado anteriormente.
            if(!Tratamento.usuarioExiste(usuario))
                // Retorna um erro.
                throw new NoSuchElementException("usuario_nao_encontrado");

            // Verifica se o produto informado existe. Caso não exista, lança exceção.
            if(!produtoRepository.existsById(produtoId))
                throw new DadosInvalidosException("produto_invalido");

            // Verifica se o mercado informado existe. Caso não exista, lança exceção.
            if(!mercadoRepository.existsById(id))
                throw new DadosInvalidosException("mercado_invalido");

            // Instanciando um objeto do estoque que será utilizado para verificar se o estoque já existe.
            Estoque estoqueComparar = new Estoque();
            // Definindo o id do produto informado pelo usuário.
            estoqueComparar.setProdutoId(produtoId);
            // Definindo o id do mercado informado pelo usuário.
            estoqueComparar.setMercadoId(id);

            // Buscando o estoque pelo produto e mercado informados.
            List<Estoque> estoquesSemelhantes = estoqueRepository.findAll(
                Example.of(
                    estoqueComparar, 
                    ExampleMatcher
                        .matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                )
            );

            // Se o estoque já existir, lança exceção.
            if(estoquesSemelhantes.size() != 0)
                throw new DadosConflitantesException("estoque_existente");

            // Definindo o id do mercado no objeto do tipo Estoque como o id enviado pela URI.
            requestEstoque.setMercadoId(id);

            // Definindo o id do produto no objeto do tipo Estoque como o id enviado pela URI.
            requestEstoque.setProdutoId(produtoId);
            
            ResponseEstoque responseEstoque = new ResponseEstoque(estoqueRepository.save(requestEstoque));

            // Adiciona à resposta um link para a leitura do estoque criado.
            responseEstoque.add(
                linkTo(
                    methodOn(EstoqueController.class).ler(responseEstoque.getId())
                )
                .withSelfRel()
            );

            // Adiciona à resposta um link para a listagem de estoques.
            responseEstoque.add(
                linkTo(
                    methodOn(EstoqueController.class).listar(new Estoque())
                )
                .withRel("collection")
            );

            // Adiciona à resposta um link para a leitura do mercado.
            responseEstoque.add(
                linkTo(
                    methodOn(MercadoController.class).ler(responseEstoque.getMercadoId())
                )
                .withRel("mercado")
            );

            // Adiciona à resposta um link para a leitura do produto.
            responseEstoque.add(
                linkTo(
                    methodOn(ProdutoController.class).ler(responseEstoque.getProdutoId())
                )
                .withRel("produto")
            );

            return responseEstoque;

        } catch (NoSuchElementException e) {
            // Lança uma exceção informando que algum registro não foi encontrado.
            throw new ResponseStatusException(404, e.getMessage(), e);
        } catch (DadosInvalidosException e) {
            // Lança uma exceção informando que há um problema no corpo da requisição.
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (DadosConflitantesException e) {
            // Lança uma exceção informando que já existe um recurso igual no banco de dados.
            throw new ResponseStatusException(409, e.getMessage(), e);
        } catch (Exception e) {
            // Lança uma exceção informando que ocorreu um erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Método responsável por criar um produto no mercado com o id informado.
     * @param id - Id do mercado que será usado como parâmetro.
     * @return ResponseEstoqueProduto - Objeto do produto criado com o id do estoque criado.
     */
    @PostMapping("/{id}/produto")
    public ResponseEstoqueProduto criarProduto(
        @PathVariable("id") Integer id,
        @RequestBody Produto requestProduto
    ) {
        try {

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

            // Obtendo o usuário informado.
            Usuario usuario = usuarioRepository.findById(requestProduto.getCriadoPor()).get();

            // Verifica se o usuário informado como criador não foi deletado anteriormente.
            if(!Tratamento.usuarioExiste(usuario))
                // Retorna um erro.
                throw new NoSuchElementException("usuario_nao_encontrado");

            // Verifica se o mercado informado existe. Caso não exista, lança exceção.
            if(!mercadoRepository.existsById(id))
                throw new DadosInvalidosException("mercado_invalido");

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
            ResponseEstoqueProduto responseProduto = new ResponseEstoqueProduto(produtoRepository.save(produtoTratado));

            // Criando uma instância de um objeto de estoque que será adicionado ao banco.
            Estoque novoEstoque = new Estoque();

            // Definindo o id do mercado no objeto do tipo Estoque como o id do mercado enviado pela URI.
            novoEstoque.setMercadoId(id);

            // Definindo o id do produto no objeto do tipo Estoque como o id do novo produto criado.
            novoEstoque.setProdutoId(responseProduto.getId());
            
            // Definindo o id do usuário no objeto do tipo Estoque como o id do usuário enviado pelo corpo da requisição.
            novoEstoque.setCriadoPor(requestProduto.getCriadoPor());

            // Criando o novo estoque no banco de dados.
            novoEstoque = estoqueRepository.save(novoEstoque);

            // Definindo o id do estoque no objeto do tipo ResponseEstoqueProduto como o id do novo estoque criado.
            responseProduto.setEstoqueId(novoEstoque.getId());

            // Adiciona à resposta um link para a leitura do produto criado.
            responseProduto.add(
                linkTo(
                    methodOn(ProdutoController.class).ler(responseProduto.getId())
                )
                .withSelfRel()
            );

            // Adiciona à resposta um link para a listagem de produtos.
            responseProduto.add(
                linkTo(
                    methodOn(ProdutoController.class).listar(new Produto())
                )
                .withRel("collection")
            );

            // Adiciona à resposta um link para a leitura do mercado.
            responseProduto.add(
                linkTo(
                    methodOn(MercadoController.class).ler(id)
                )
                .withRel("mercado")
            );

            // Adiciona à resposta um link para a leitura do produto.
            responseProduto.add(
                linkTo(
                    methodOn(ProdutoController.class).ler(responseProduto.getEstoqueId())
                )
                .withRel("estoque")
            );

            return responseProduto;

        } catch (NoSuchElementException e) {
            // Lança uma exceção informando que algum registro não foi encontrado.
            throw new ResponseStatusException(404, e.getMessage(), e);
        } catch (DadosInvalidosException e) {
            // Lança uma exceção informando que há um problema no corpo da requisição.
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (DadosConflitantesException e) {
            // Lança uma exceção informando que já existe um recurso igual no banco de dados.
            throw new ResponseStatusException(409, e.getMessage(), e);
        } catch (Exception e) {
            // Lança uma exceção informando que ocorreu um erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Método responsável por criar uma sugestão em um produto específico no mercado informado.
     * @param id - Id do mercado que será utilizado como parâmetro.
     * @param produtoId - Id do produto que terá a sugestão de preço criada.
     * @return ResponseSugestao - Objeto da sugestão criada.
     */
    @PostMapping("/{id}/produto/{produtoId}/sugestao")
    public ResponseSugestao criarSugestao(
        @PathVariable("id") Integer id,
        @PathVariable(value = "produtoId") Integer produtoId,
        @RequestBody Sugestao requestSugestao
    ) {
        try {

            // Validando os dados enviados pelo cliente.
            Tratamento.validarSugestao(requestSugestao, true);

            // Verifica se o cliente informou o id do usuário criador.
            if(requestSugestao.getCriadoPor() == null)
                // Lança exceção informando que o cliente não informou o id do usuário criador.
                throw new DadosInvalidosException("usuario_invalido");

            // Verifica se o cliente informou o preço da sugestão.
            if(requestSugestao.getPreco() == null)
                // Lança exceção informando que o cliente não informou o preço da sugestão.
                throw new DadosInvalidosException("preco_invalido");

            // Se o usuário informado não existir,
            if(!usuarioRepository.existsById(requestSugestao.getCriadoPor()))
                // Retorna erro.
                throw new DadosInvalidosException("usuario_nao_encontrado");

            Usuario usuario = usuarioRepository.findById(requestSugestao.getCriadoPor()).get();

            // Verifica se o produto informado existe. Caso não exista, lança exceção.
            if(!produtoRepository.existsById(produtoId))
                throw new DadosInvalidosException("produto_invalido");

            // Verifica se o mercado informado existe. Caso não exista, lança exceção.
            if(!mercadoRepository.existsById(id))
                throw new DadosInvalidosException("mercado_invalido");

            // Validando se o usuário informado como criador já foi excluído.
            if(!Tratamento.usuarioExiste(usuario))
                // Retorna erro.
                throw new NoSuchElementException("usuario_nao_encontrado");

            // Buscando o registro do estoque do mercado, que associa o produto ao estoque
            // do mercado
            Estoque estoque = estoqueRepository.findByProdutoIdAndMercadoId(produtoId, id);

            // Se o estoque for nulo
            if(estoque == null)
                // Lança uma exceção informando que o produto não está no estoque do mercado.
                throw new NoSuchElementException("estoque_nao_encontrado");

            // Adicionando à sugestão que será criada o id do estoque.
            requestSugestao.setEstoqueId(estoque.getId());

            // Elimina os decimais do preço multiplicando por 100.
            requestSugestao.setPreco(requestSugestao.getPreco() * 100);

            // Insere a sugestão e transforma os dados obtidos em modelo de resposta.
            ResponseSugestao responseSugestao = new ResponseSugestao(sugestaoRepository.save(requestSugestao));

            // Adiciona o link para a leitura da sugestão criada.
            responseSugestao.add(
                linkTo(
                    methodOn(SugestaoController.class).ler(responseSugestao.getId())
                )
                .withSelfRel()
            );

            // Adiciona o link para a listagem de sugestões.
            responseSugestao.add(
                linkTo(
                    methodOn(SugestaoController.class).listar(new Sugestao())
                )
                .withRel("collection")
            );

            // Adiciona o link para a leitura do estoque da sugestão criada.
            responseSugestao.add(
                linkTo(
                    methodOn(EstoqueController.class).ler(responseSugestao.getEstoqueId())
                )
                .withRel("estoque")
            );

            // Divide o preço por 100 para obter os centavos.
            responseSugestao.setPreco(responseSugestao.getPreco() / 100);

            // Retorna a sugestão criada.
            return responseSugestao;

        } catch (NoSuchElementException e) {
            // Lança uma exceção informando que algum registro não foi encontrado.
            throw new ResponseStatusException(404, e.getMessage(), e);
        } catch (DadosInvalidosException e) {
            // Lança uma exceção informando que o corpo da requisição é inválido.
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (Exception e) {
            // Lança uma exceção informando que ocorreu um erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Método responsável por ler um mercado com o id informado.
     * @param id - Id do mercado que será lido.
     * @return ResponseMercado - Objeto do tipo ResponseMercado que contém os dados do mercado lido.
     */
    @GetMapping("/{id}")
    public ResponseMercado ler(@PathVariable("id") Integer id) {
        try {
            // Busca o mercado com o id informado e armazena num objeto do tipo ResponseMercado.
            ResponseMercado responseMercado = new ResponseMercado(mercadoRepository.findById(id).get());

            // Se houver um mercado com o id informado
            if(responseMercado != null) {

                // Adiciona à resposta um link para listar todos os mercados
                responseMercado.add(
                    linkTo(
                        methodOn(MercadoController.class).listar(new Mercado())
                    )
                    .withRel("collection")
                );

            }

            // Retorna o objeto do tipo ResponseMercado com o mercado lido e o link para a listagem dos mercados
            return responseMercado;

        } catch (NoSuchElementException e) {
            // Lança uma exceção informando que o mercado com o id informado não foi encontrado.
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (Exception e) {
            // Lança uma exceção informando que ocorreu um erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Método responsável por listar todos os produtos de um mercado informado.
     * @param id - Id do mercado que será lido.
     * @return <b>List < ResponseProduto ></b> - Lista de objetos ResponseProduto que contém todos os produtos do mercado.
     */
    @GetMapping("/{id}/produto")
    public List<ResponseProduto> listarProdutos(@PathVariable("id") Integer id) {
        try {
            // Verifica se o mercado informado existe. Caso não exista, lança exceção.
            if(!mercadoRepository.existsById(id))
                throw new NoSuchElementException("mercado_nao_encontrado");

            // Busca todos os estoques do mercado com o id informado e os armazena numa lista.
            List<Estoque> estoques = estoqueRepository.findByMercadoId(id);
            
            // Se não houver estoques no mercado
            if(estoques.isEmpty())
                // Lança uma exceção informando que o mercado com o id informado não possui nenhum estoque.
                throw new NoSuchElementException("estoque_nao_encontrado");

            // Cria uma lista de objetos ResponseProduto.
            List<ResponseProduto> responseProduto = new ArrayList<>();

            // Para cada estoque do mercado
            for(Estoque estoque : estoques){
                // Pesquisa as informações do produto do estoque atual e adiciona à lista de resposta de produtos.
                responseProduto.add(new ResponseProduto(produtoRepository.findById(estoque.getProdutoId()).get()));
            }

            // Para cada produto da lista de resposta
            for(ResponseProduto produto : responseProduto){

                // Adiciona à resposta um link para a leitura do produto em questão.
                produto.add(
                    linkTo(
                        methodOn(ProdutoController.class).ler(produto.getId())
                    )
                    .withSelfRel()
                );

                // Adiciona à resposta um link para listar todos os produtos.
                produto.add(
                    linkTo(
                        methodOn(ProdutoController.class).listar(new Produto())
                    )
                    .withRel("collection")
                );

            }

            // Retorna o objeto do tipo ResponseMercado com o mercado lido e o link para a listagem dos mercados
            return responseProduto;

        } catch (NoSuchElementException e) {
            // Lança uma exceção informando que o mercado com o id informado não foi encontrado.
            throw new ResponseStatusException(404, e.getMessage(), e);
        } catch (Exception e) {
            // Lança uma exceção informando que ocorreu um erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Método responsável por retornar o estoque do mercado com o produto informado.
     * @param id - Id do mercado que será buscado como parâmetro.
     * @return ResponseEstoque - Objeto de resposta do estoque do mercado com o produto informado.
     */
    @GetMapping("/{id}/produto/{produtoId}")
    public ResponseEstoque lerEstoque(
        @PathVariable("id") Integer id, 
        @PathVariable(value = "produtoId") Integer produtoId) 
    {
        try {

            // Verifica se o mercado informado existe. Caso não exista, lança exceção.
            if(!mercadoRepository.existsById(id))
                throw new NoSuchElementException("mercado_nao_encontrado");

            // Verifica se o produto informado existe. Caso não exista, lança exceção.
            if(!produtoRepository.existsById(produtoId))
                throw new NoSuchElementException("produto_nao_encontrado");

            // Busca um estoque do mercado com o id do mercado e produto informado
            ResponseEstoque responseEstoque = new ResponseEstoque(estoqueRepository.findByProdutoIdAndMercadoId(produtoId, id));

            // Adiciona à resposta um link para a leitura do estoque em questão.
            responseEstoque.add(
                linkTo(
                    methodOn(EstoqueController.class).ler(responseEstoque.getId())
                )
                .withSelfRel()
            );

            // Adiciona à resposta um link para listar todos os estoques.
            responseEstoque.add(
                linkTo(
                    methodOn(EstoqueController.class).listar(new Estoque())
                )
                .withRel("collection")
            );

            // Adiciona à resposta um link para a leitura do produto.
            responseEstoque.add(
                linkTo(
                    methodOn(ProdutoController.class).ler(responseEstoque.getProdutoId())
                )
                .withRel("produto")
            );

            // Adiciona à resposta um link para a leitura do mercado.
            responseEstoque.add(
                linkTo(
                    methodOn(MercadoController.class).ler(responseEstoque.getMercadoId())
                )
                .withRel("mercado")
            );

            // Retorna o objeto do tipo ResponseMercado com o mercado lido e o link para a listagem dos mercados
            return responseEstoque;

        } catch (NoSuchElementException e) {
            // Lança uma exceção informando que o mercado com o id informado não foi encontrado.
            throw new ResponseStatusException(404, e.getMessage(), e);
        } catch (Exception e) {
            // Lança uma exceção informando que ocorreu um erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Método responsável por listar todas as sugestões de um produto específico no mercado.
     * @param id - Id do mercado que será buscado como parâmetro.
     * @param produtoId - Id do produto no qual será buscadas as sugestões.
     * @return <b>List < ResponseSugestao ></b> - Lista de sugestões do produto específico no mercado.
     */
    @GetMapping("/{id}/produto/{produtoId}/sugestao")
    public List<ResponseSugestao> ler(
        @PathVariable("id") Integer id,
        @PathVariable(value = "produtoId") Integer produtoId
    ) {
        try {

            // Criando uma lista de respostas de sugestões vazia.
            List<ResponseSugestao> responseSugestao = new ArrayList<ResponseSugestao>();

            // Buscando o registro do estoque do mercado, que associa o produto ao estoque
            // do mercado
            Estoque estoque = estoqueRepository.findByProdutoIdAndMercadoId(produtoId, id);

            // Se o estoque for nulo
            if(estoque == null)
                // Lança uma exceção informando que o produto não está no estoque do mercado.
                throw new NoSuchElementException("estoque_nao_encontrado");

            // Buscando todas as sugestões de preço do produto no mercado informado
            List<Sugestao> sugestoes = sugestaoRepository.findByEstoqueId(estoque.getId());

            // Se não houver sugestões para esse produto
            if(sugestoes == null)
                // Lança uma exceção informando que não há sugestões para esse produto.
                throw new NoSuchElementException("sugestao_nao_encontrado");

            // Para cada sugestão encontrada
            for(Sugestao sugestao : sugestoes) {
                // Cria um objeto do tipo ResponseSugestao, convertendo o objeto Sugestao para o objeto ResponseSugestao
                responseSugestao.add(new ResponseSugestao(sugestao));
            }

            // Para cada sugestão da resposta
            for(ResponseSugestao sugestao : responseSugestao) {
                // Converte o preço da sugestão de int para float para ser exibido no formato de moeda no retorno.
                sugestao.setPreco(sugestao.getPreco() / 100);

                // Adiciona à resposta um link para a leitura da sugestão em questão.
                sugestao.add(
                    linkTo(
                        methodOn(SugestaoController.class).ler(sugestao.getId())
                    )
                    .withSelfRel()
                );
                
                // Adiciona à resposta um link para a listagem de mercados.
                sugestao.add(
                    linkTo(
                        methodOn(MercadoController.class).listar(new Mercado())
                    )
                    .withRel("collection")
                );

                // Adiciona à resposta um link para a leitura do produto em questão.
                sugestao.add(
                    linkTo(
                        methodOn(ProdutoController.class).ler(produtoId)
                    )
                    .withRel("produto")
                );

                // Adiciona à resposta um link para a leitura da sugestão em questão
                sugestao.add(
                    linkTo(
                        methodOn(MercadoController.class).ler(id)
                    )
                    .withRel("mercado")
                );

                // Adiciona à resposta um link para a leitura do estoque em questão.
                sugestao.add(
                    linkTo(
                        methodOn(EstoqueController.class).ler(estoque.getId())
                    )
                    .withRel("estoque")
                );
            }

            return responseSugestao;

        } catch (NoSuchElementException e) {
            // Lança uma exceção informando que algum registro não foi encontrado.
            throw new ResponseStatusException(404, e.getMessage(), e);
        } catch (Exception e) {
            // Lança uma exceção informando que ocorreu um erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Método responsável por listar todos os mercados.
     * @param mercado - Objeto do tipo Mercado que será usado como parâmetro para filtrar os resultados.
     * @return <b>List < ResponseMercado ></b> - Lista de mercados.
     */
    @GetMapping
    public List<ResponseMercado> listar(Mercado requestMercado) {
        try {

            // Validando o mercado enviado como parâmetro pelo cliente.
            Tratamento.validarMercado(requestMercado, true);

            // Buscando todos os mercados de acordo com o filtro enviado por parâmetro e salvando na lista de mercados
            List<Mercado> mercados = mercadoRepository.findAll(
                Example.of(
                    requestMercado, 
                    ExampleMatcher
                        .matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                )
            );

            // Criando uma lista de respostas de mercados vazia.
            List<ResponseMercado> responseMercado = new ArrayList<ResponseMercado>();

            // Para cada mercado encontrado
            for(Mercado mercado : mercados) {
                // Cria um objeto do tipo ResponseMercado, convertendo o objeto Mercado para o objeto ResponseMercado
                responseMercado.add(new ResponseMercado(mercado));
            }

            // Se a lista de marcados a serem retornados não for vazia
            if(!responseMercado.isEmpty()) {
                // Para cada marcado da resposta
                for(ResponseMercado mercado : responseMercado) {
                    // Adiciona à resposta um link para a leitura do marcado em questão.
                    mercado.add(
                        linkTo(
                            methodOn(MercadoController.class).ler(mercado.getId())
                        )
                        .withSelfRel()
                    );
                }
            }

            // Retorna a lista de marcados encontrados.
            return responseMercado;

        } catch (DadosInvalidosException e) {
            // Lança uma exceção informando que os dados enviados pelo cliente são inválidos.
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (NullPointerException e) {
            // Lança uma exceção caso algum registro seja nulo
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (UnsupportedOperationException e) {
            // Lança uma exceção caso ocorra algum erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (Exception e) {
            // Lança uma exceção caso ocorra algum erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Método responsável por listar todos os mercados com paginação.
     * @param requestMercado - Objeto do mercado que será usado como parâmetro para buscar o registro.
     * @param pagina - Número da página que será exibida, contada a partir do 0.
     * @param limite - Número de registros que serão exibidos por página.
     * @return <b>ResponseMercado</b> - Mercado encontrado.
     */
    @GetMapping(params = { "pagina", "limite" })
    public ResponsePagina listar(
        Mercado requestMercado,
        @RequestParam(required = false, defaultValue = "0") Integer pagina,
        @RequestParam(required = false, defaultValue = "10") Integer limite
    ) {
        try {
            // Validando o mercado enviado como parâmetro pelo cliente.
            Tratamento.validarMercado(requestMercado, true);

            // Buscando todos os mercados de acordo com o filtro enviado por parâmetro e salvando na página de Mercados.
            Page<Mercado> paginaMercado = mercadoRepository.findAll(
                Example.of(
                    requestMercado, 
                    ExampleMatcher
                        .matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                ),
                PageRequest.of(pagina, limite)
            );

            // Criando uma lista de resposta de mercados vazia.
            List<ResponseMercado> mercados = new ArrayList<ResponseMercado>();

            // Para cada mercado encontrado
            for(Mercado mercado : paginaMercado.getContent()) {
                // Cria um objeto do tipo ResponseMercado, convertendo o objeto Mercado para o objeto ResponseMercado
                mercados.add(new ResponseMercado(mercado));
            }

            // Criando um objeto do tipo ResponsePagina, que será retornado com os mercados e os dados de paginação.
            ResponsePagina responseMercado = PaginaUtils.criarResposta(pagina, limite, paginaMercado, mercados);

            // Adiciona à resposta um link para a primeira página da listagem de mercados.
            responseMercado.add(
                linkTo(
                    methodOn(MercadoController.class).listar(requestMercado, 0, limite)
                )
                .withRel("first")
            );

            // Se a página de mercados não estiver vazia.
            if(!paginaMercado.isEmpty()) {
                // Se a página informada pelo cliente não for a primeira página da listagem de mercados.
                if(pagina > 0) {
                    // Adiciona à resposta um link para a página anterior da listagem de mercados.
                    responseMercado.add(
                        linkTo(
                            methodOn(MercadoController.class).listar(requestMercado, pagina - 1, limite)
                        )
                        .withRel("previous")
                    );
                }

                // Se a página informada pelo cliente não for a última página da listagem de mercados.
                if(pagina < paginaMercado.getTotalPages() - 1) {
                    // Adiciona à resposta um link para a página seguinte da listagem de mercados.
                    responseMercado.add(
                        linkTo(
                            methodOn(MercadoController.class).listar(requestMercado, pagina + 1, limite)
                        )
                        .withRel("next")
                    );
                }

                // Adiciona à resposta um link para a última página da listagem de mercados.
                responseMercado.add(
                    linkTo(
                        methodOn(MercadoController.class).listar(requestMercado, paginaMercado.getTotalPages() - 1, limite)
                    )
                    .withRel("last")
                );

                // Para cada marcado da resposta
                for(ResponseMercado mercado : mercados) {
                    // Adiciona à resposta um link para a leitura do marcado em questão.
                    mercado.add(
                        linkTo(
                            methodOn(MercadoController.class).ler(mercado.getId())
                        )
                        .withSelfRel()
                    );
                }
            }

            // Retorna a página com os marcados encontrados e as informações da paginação.
            return responseMercado;

        } catch (DadosInvalidosException e) {
            // Lança uma exceção informando que os dados enviados pelo cliente são inválidos.
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            // Lança uma exceção caso ocorra algum erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (NullPointerException e) {
            // Lança uma exceção caso algum registro seja nulo
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (Exception e) {
            // Lança uma exceção caso ocorra algum erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Método responsável por atualizar alguns atributos específicos de um mercado.
     * @param id - Id do mercado que será atualizado.
     * @param requestMercado - Parâmetros que serão atualizados no mercado.
     * @return <b>ResponseMercado</b> - Mercado atualizado.
     */
    @PatchMapping("/{id}")
    public ResponseMercado editar(@PathVariable int id, @RequestBody Mercado requestMercado) {
        try {

            // Validando os parâmetros enviados pelo cliente.
            Tratamento.validarMercado(requestMercado, true);

            // Buscando um mercado que possua o mesmo endereço informado
            if(mercadoRepository.findByEndereco(
                requestMercado.getLogradouro(), 
                requestMercado.getNumero(),
                requestMercado.getComplemento(), 
                requestMercado.getBairro(), 
                requestMercado.getCidade(),
                requestMercado.getUf(), 
                requestMercado.getCep()) != null
            )
                // Lança uma exceção informando que o endereço já existe.
                throw new DadosConflitantesException("mercado_existente");
            
            // Buscando o mercado que será atualizado e guardando seu estado atual.
            Mercado mercadoAtual = mercadoRepository.findById(id).get();

            // Se o ramo informado não existir
            if(!ramoRepository.existsById(requestMercado.getRamoId()))
                // Lança uma exceção informando que o ramo informado não existe.
                throw new DadosInvalidosException("ramo_nao_encontrado");

            // Se o nome do mercado já existir
            if(mercadoRepository.existsByNomeIgnoreCase(requestMercado.getNome()))
                // Lança uma exceção informando que o nome do mercado já existe.
                throw new DadosConflitantesException("mercado_existente");

            // Atualizando o mercado com os dados enviados pelo cliente.
            ResponseMercado responseMercado = new ResponseMercado(
                mercadoRepository.save(
                    // Chamando método que retornará o mercado com os dados atualizados de acordo com os parâmetros recebidos.
                    EditaRecurso.editarMercado(
                        mercadoAtual, 
                        requestMercado
                    )
                )
            );

            // Adiciona à resposta um link para a leitura do mercado em questão.
            responseMercado.add(
                linkTo(
                    methodOn(MercadoController.class).ler(responseMercado.getId())
                )
                .withSelfRel()
            );

            // Retorna o mercado atualizado.
            return responseMercado;

        } catch (DadosConflitantesException e) {
            // Lança uma exceção informando que os dados enviados pelo cliente são conflitantes/já existem no banco de dados.
            throw new ResponseStatusException(409, e.getMessage(), e);
        } catch (DadosInvalidosException e) {
            // Lança uma exceção informando que os dados enviados pelo cliente são inválidos.
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (DataIntegrityViolationException e) {
            // Lança uma exceção informando que ocorreu um erro de integridade de dados.
            throw new ResponseStatusException(500, "erro_insercao", e);
        } catch (IllegalArgumentException e) {
            // Lança uma exceção caso ocorra algum erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (NoSuchElementException e) {
            // Lança uma exceção caso algum registro não seja encontrado.
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (Exception e) {
            // Lança uma exceção caso ocorra algum erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Método responsável por atualizar todos os atributos de um mercado.
     * @param id - Id do mercado que será atualizado.
     * @param requestMercado - Parâmetros que serão atualizados no mercado.
     * @return <b>ResponseMercado</b> - Mercado atualizado.
     */
    @PutMapping("/{id}")
    public ResponseMercado atualizar(@PathVariable int id, @RequestBody Mercado requestMercado) {
        try {

            // Validando os parâmetros enviados pelo cliente.
            Tratamento.validarMercado(requestMercado, false);

            // Se o id do mercado informado não existir
            if(!mercadoRepository.existsById(id))
                // Lança uma exceção informando que o mercado não existe.
                throw new NoSuchElementException("nao_encontrado");

            // Se o id do usuário informado não existir
            if(!usuarioRepository.existsById(requestMercado.getCriadoPor()))
                // Lança uma exceção informando que o usuário não existe.
                throw new DadosInvalidosException("usuario_nao_encontrado");

            // Se o id do ramo informado não existir
            if(!ramoRepository.existsById(requestMercado.getRamoId()))
                // Lança uma exceção informando que o ramo informado não existe.
                throw new DadosInvalidosException("ramo_nao_encontrado");

            // Se o nome do mercado já existir no banco de dados
            if(mercadoRepository.existsByNomeIgnoreCase(requestMercado.getNome()))
                // Lança uma exceção informando que o nome do mercado já existe.
                throw new DadosConflitantesException("mercado_existente");

            // Se o endereço do mercado já existir no banco de dados
            if(mercadoRepository.findByEndereco(
                requestMercado.getLogradouro(), 
                requestMercado.getNumero(),
                requestMercado.getComplemento(), 
                requestMercado.getBairro(), 
                requestMercado.getCidade(),
                requestMercado.getUf(), 
                requestMercado.getCep()) != null
            )
                // Lança uma exceção informando que o endereço já existe.
                throw new DadosConflitantesException("mercado_existente");

            Mercado mercado = mercadoRepository.findById(id).get();

            // Adiciona ao objeto da requisição o Id informado pelo cliente.
            requestMercado.setId(id);

            // Adiciona ao objeto da requisição o Id do usuário que criou o mercado.
            requestMercado.setCriadoPor(mercado.getCriadoPor());

            // Atualizando o mercado com os dados enviados pelo cliente e armazenando no objeto responseMercado.
            ResponseMercado responseMercado = new ResponseMercado(mercadoRepository.save(requestMercado));

            // Adiciona à resposta um link para a leitura do mercado em questão.
            responseMercado.add(
                linkTo(
                    methodOn(MercadoController.class).ler(responseMercado.getId())
                )
                .withSelfRel()
            );

            // Retorna o mercado atualizado.
            return responseMercado;

        } catch (DadosConflitantesException e) {
            // Lança uma exceção informando que os dados enviados pelo cliente são conflitantes/já existem no banco de dados.
            throw new ResponseStatusException(409, e.getMessage(), e);
        } catch (DadosInvalidosException e) {
            // Lança uma exceção informando que os dados enviados pelo cliente são inválidos.
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (NoSuchElementException e) {
            // Lança uma exceção informando que algum registro não foi encontrado.
            throw new ResponseStatusException(404, e.getMessage(), e);
        } catch (DataIntegrityViolationException e) {
            // Lança uma exceção informando que ocorreu um erro de integridade de dados.
            throw new ResponseStatusException(500, "erro_insercao", e);
        } catch (IllegalArgumentException e) {
            // Lança uma exceção caso ocorra algum erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (Exception e) {
            // Lança uma exceção caso ocorra algum erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Método responsável por deletar um mercado.
     * @param id - Id do mercado que será deletado.
     * @return Object - Link para a listagem de mercados.
     */
    @DeleteMapping("/{id}")
    public Object remover(@PathVariable int id) {
        try {

            // Se o id do mercado informado não existir
            if(!mercadoRepository.existsById(id))
                // Lança uma exceção informando que o mercado não existe.
                throw new NoSuchElementException("nao_encontrado");

            // Deleta o mercado com o id informado.
            mercadoRepository.deleteById(id);

            // Retorna o link para a listagem de mercados.
            return linkTo(
                        methodOn(MercadoController.class).listar(new Mercado())
                    )
                    .withRel("collection");

        } catch (NoSuchElementException e) {
            // Lança uma exceção informando que o mercado não existe.
            throw new ResponseStatusException(404, e.getMessage(), e);
        } catch (DataIntegrityViolationException e) {
            // Lança uma exceção informando que ocorreu um erro de integridade de dados.
            throw new ResponseStatusException(500, "erro_remocao", e);
        } catch (IllegalArgumentException e) {
            // Lança uma exceção caso ocorra algum erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (Exception e) {
            // Lança uma exceção caso ocorra algum erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }
}
