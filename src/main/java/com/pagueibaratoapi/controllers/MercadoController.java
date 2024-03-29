package com.pagueibaratoapi.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
    @CacheEvict(value = "mercados", allEntries = true)
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
    @CacheEvict(value = "estoques", allEntries = true)
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
    @Caching(evict = {
        @CacheEvict(value = "mercadoProdutos", allEntries = true),
        @CacheEvict(value = "estoques", allEntries = true)
    })
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
    @CacheEvict(value = "mercadoSugestoes", allEntries = true)
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
    @Cacheable("mercadoProdutos")
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
     * Sobrecarga do método responsável por listar todos os produtos de um mercado informado. Pode ser paginado.
     * @param id - Id do mercado que será lido.
     * @param pagina - Número da página que será listada.
     * @param limite - Número de registros que serão listados por página.
     * @return <b>ResponsePagina</b> - Objeto da página com as informações de paginação e os itens da página atual.
     */
    @GetMapping(value = "/{id}/produto", params = {"pagina", "limite"})
    @Cacheable("mercadoProdutos")
    public ResponsePagina listarProdutos(
        @PathVariable("id") Integer id,
        @RequestParam(required = false, defaultValue = "0") Integer pagina,
        @RequestParam(required = false, defaultValue = "10") Integer limite
    ) {
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
            List<ResponseProduto> produtos = new ArrayList<>();

            // Para cada estoque do mercado
            for(Estoque estoque : estoques){
                // Pesquisa as informações do produto do estoque atual e adiciona à lista de resposta de produtos.
                produtos.add(new ResponseProduto(produtoRepository.findById(estoque.getProdutoId()).get()));
            }

            // Para cada produto da lista de resposta
            for(ResponseProduto produto : produtos){

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

            // Produtos que serão mostrados na página atual.
            // O início da página é a multiplicação do número da página pelo limite de produtos por página.
            // O fim da página é o inicío da página mais o limite de produtos por página.
            List<ResponseProduto> produtosPagina = produtos.subList(pagina * limite, (pagina * limite) + limite);

            // Representa o total de páginas da pesquisa.
            // O total de páginas é calculado dividindo o total de produtos pelo limite de produtos por página.
            // O valor é arredondado para cima porque existe a possibilidade da ultima página não possuir o número limite de produtos.
            Integer totalPaginas = (int) Math.ceil(produtos.size() / (double) limite);

            // Total de produtos da pesquisa.
            // Se o tamanho for maior que Integer.MAX_VALUE, o valor é Integer.MAX_VALUE.
            Integer totalRegistros = produtos.size();

            // Prepara uma resposta em formato de página
            ResponsePagina responseProduto = PaginaUtils.criarResposta(pagina, limite, totalRegistros, totalPaginas, produtosPagina);

            // Adiciona à resposta um link para a primeira página da listagem de produtos.
            responseProduto.add(
                linkTo(
                    methodOn(MercadoController.class).listarProdutos(id, 0, limite)
                )
                .withRel("first")
            );

            // Se a página de produtos não estiver vazia.
            if(!produtos.isEmpty()) {
                // Se a página informada pelo cliente não for a primeira página da listagem de produtos.
                if(pagina > 0) {
                    // Adiciona à resposta um link para a página anterior da listagem de produtos.
                    responseProduto.add(
                        linkTo(
                            methodOn(MercadoController.class).listarProdutos(id, pagina - 1, limite)
                        )
                        .withRel("previous")
                    );
                }

                // Se a página informada pelo cliente não for a última página da listagem de produtos.
                if(pagina < totalPaginas - 1) {
                    // Adiciona à resposta um link para a página seguinte da listagem de produtos.
                    responseProduto.add(
                        linkTo(
                            methodOn(MercadoController.class).listarProdutos(id, pagina + 1, limite)
                        )
                        .withRel("next")
                    );
                }

                // Adiciona à resposta um link para a última página da listagem de produtos.
                responseProduto.add(
                    linkTo(
                        methodOn(MercadoController.class).listarProdutos(id, totalPaginas - 1, limite)
                    )
                    .withRel("last")
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
     * Sobrecarga do método responsável por listar todos os produtos de um mercado informado. Pode ser ordenado.
     * @param id - Id do mercado que será lido.
     * @param ordenarPor - Campo do banco de dados que servirá de parâmetro para ordenar.
     * @param ordem - Direção em que os dados serão ordenados entre "asc" e "desc".
     * @return <b>List < ResponseProduto ></b> - Lista ordenada dos produtos do mercado.
     */
    @GetMapping(value = "/{id}/produto", params = {"ordenarPor", "ordem"})
    @Cacheable("mercadoProdutos")
    public List<ResponseProduto> listarProdutos(
        @PathVariable("id") Integer id,
        @RequestParam(required = false, defaultValue = "") String ordenarPor,
        @RequestParam(required = false, defaultValue = "asc") String ordem
    ) {
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

            // Faz a ordenação da lista de produtos.
            responseProduto.sort((o1, o2) -> {

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
                    
                    case "marca":

                        // Se a ordem for "asc"
                        if(ordem.equals("asc"))
                            // Ordena a lista de produtos por marca ascendente.
                            return o1.getMarca().compareTo(o2.getMarca());
                        // Se a ordem for "desc"
                        else
                            // Ordena a lista de produtos por marca descendente.
                            return o2.getMarca().compareTo(o1.getMarca());
                    
                    case "tamanho":

                        // Se a ordem for "asc"
                        if(ordem.equals("asc"))
                            // Ordena a lista de produtos por tamanho ascendente.
                            return o1.getTamanho().compareTo(o2.getTamanho());
                        // Se a ordem for "desc"
                        else
                            // Ordena a lista de produtos por tamanho descendente.
                            return o2.getTamanho().compareTo(o1.getTamanho());
                    
                    case "cor":

                        // Se a ordem for "asc"
                        if(ordem.equals("asc"))
                            // Ordena a lista de produtos por cor ascendente.
                            return o1.getCor() != null ? o1.getCor().compareTo(o2.getCor()) : 0;
                        // Se a ordem for "desc"
                        else
                            // Ordena a lista de produtos por cor descendente.
                            return o2.getCor() != null ? o2.getCor().compareTo(o1.getCor()) : 0;
                
                    default:
                        return 0;
                }
            });

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
     * Sobrecarga do método responsável por listar todos os produtos de um mercado informado. Pode ser paginado e ordenado.
     * @param id - Id do mercado que será lido.
     * @param pagina - Número da página que será listada.
     * @param limite - Número de registros que serão listados por página.
     * @param ordenarPor - Campo do banco de dados que servirá de parâmetro para ordenar.
     * @param ordem - Direção em que os dados serão ordenados entre "asc" e "desc".
     * @return <b>ResponsePagina</b> - Objeto da página com as informações de paginação e os itens da página atual ordenados.
     */
    @GetMapping(value = "/{id}/produto", params = {"pagina", "limite", "ordenarPor", "ordem"})
    @Cacheable("mercadoProdutos")
    public ResponsePagina listarProdutos(
        @PathVariable("id") Integer id,
        @RequestParam(required = false, defaultValue = "0") Integer pagina,
        @RequestParam(required = false, defaultValue = "10") Integer limite,
        @RequestParam(required = false, defaultValue = "") String ordenarPor,
        @RequestParam(required = false, defaultValue = "asc") String ordem
    ) {
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
            List<ResponseProduto> produtos = new ArrayList<>();

            // Para cada estoque do mercado
            for(Estoque estoque : estoques){
                // Pesquisa as informações do produto do estoque atual e adiciona à lista de resposta de produtos.
                produtos.add(new ResponseProduto(produtoRepository.findById(estoque.getProdutoId()).get()));
            }

            // Faz a ordenação da lista de produtos.
            produtos.sort((o1, o2) -> {

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
                    
                    case "marca":

                        // Se a ordem for "asc"
                        if(ordem.equals("asc"))
                            // Ordena a lista de produtos por marca ascendente.
                            return o1.getMarca().compareTo(o2.getMarca());
                        // Se a ordem for "desc"
                        else
                            // Ordena a lista de produtos por marca descendente.
                            return o2.getMarca().compareTo(o1.getMarca());
                    
                    case "tamanho":

                        // Se a ordem for "asc"
                        if(ordem.equals("asc"))
                            // Ordena a lista de produtos por tamanho ascendente.
                            return o1.getTamanho().compareTo(o2.getTamanho());
                        // Se a ordem for "desc"
                        else
                            // Ordena a lista de produtos por tamanho descendente.
                            return o2.getTamanho().compareTo(o1.getTamanho());
                    
                    case "cor":

                        // Se a ordem for "asc"
                        if(ordem.equals("asc"))
                            // Ordena a lista de produtos por cor ascendente.
                            return o1.getCor() != null ? o1.getCor().compareTo(o2.getCor()) : 0;
                        // Se a ordem for "desc"
                        else
                            // Ordena a lista de produtos por cor descendente.
                            return o2.getCor() != null ? o2.getCor().compareTo(o1.getCor()) : 0;
                
                    default:
                        return 0;
                }
            });

            // Para cada produto da lista de resposta
            for(ResponseProduto produto : produtos){

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

            // Produtos que serão mostrados na página atual.
            // O início da página é a multiplicação do número da página pelo limite de produtos por página.
            // O fim da página é o inicío da página mais o limite de produtos por página.
            List<ResponseProduto> produtosPagina = produtos.subList(pagina * limite, (pagina * limite) + limite);

            // Representa o total de páginas da pesquisa.
            // O total de páginas é calculado dividindo o total de produtos pelo limite de produtos por página.
            // O valor é arredondado para cima porque existe a possibilidade da ultima página não possuir o número limite de produtos.
            Integer totalPaginas = (int) Math.ceil(produtos.size() / (double) limite);

            // Total de produtos da pesquisa.
            // Se o tamanho for maior que Integer.MAX_VALUE, o valor é Integer.MAX_VALUE.
            Integer totalRegistros = produtos.size();

            // Prepara uma resposta em formato de página
            ResponsePagina responseProduto = PaginaUtils.criarResposta(pagina, limite, totalRegistros, totalPaginas, produtosPagina);

            // Adiciona à resposta um link para a primeira página da listagem de produtos.
            responseProduto.add(
                linkTo(
                    methodOn(MercadoController.class).listarProdutos(id, 0, limite)
                )
                .withRel("first")
            );

            // Se a página de produtos não estiver vazia.
            if(!produtos.isEmpty()) {
                // Se a página informada pelo cliente não for a primeira página da listagem de produtos.
                if(pagina > 0) {
                    // Adiciona à resposta um link para a página anterior da listagem de produtos.
                    responseProduto.add(
                        linkTo(
                            methodOn(MercadoController.class).listarProdutos(id, pagina - 1, limite)
                        )
                        .withRel("previous")
                    );
                }

                // Se a página informada pelo cliente não for a última página da listagem de produtos.
                if(pagina < totalPaginas - 1) {
                    // Adiciona à resposta um link para a página seguinte da listagem de produtos.
                    responseProduto.add(
                        linkTo(
                            methodOn(MercadoController.class).listarProdutos(id, pagina + 1, limite)
                        )
                        .withRel("next")
                    );
                }

                // Adiciona à resposta um link para a última página da listagem de produtos.
                responseProduto.add(
                    linkTo(
                        methodOn(MercadoController.class).listarProdutos(id, totalPaginas - 1, limite)
                    )
                    .withRel("last")
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
    @Cacheable("mercadoSugestoes")
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
     * Sobrecarga do método responsável por listar todas as sugestões de um produto específico no mercado. Pode ser paginado.
     * @param id - Id do mercado que será buscado como parâmetro.
     * @param produtoId - Id do produto no qual será buscadas as sugestões.
     * @param pagina - Número da página que será listada.
     * @param limite - Número de registros que serão listados por página.
     * @return <b>ResponsePagina</b> - Objeto contendo as informações da página e a lista de sugestões.
     */
    @GetMapping(value = "/{id}/produto/{produtoId}/sugestao", params = {"pagina", "limite"})
    @Cacheable("mercadoSugestoes")
    public ResponsePagina ler(
        @PathVariable("id") Integer id,
        @PathVariable(value = "produtoId") Integer produtoId,
        @RequestParam(required = false, defaultValue = "0") Integer pagina,
        @RequestParam(required = false, defaultValue = "10") Integer limite
    ) {
        try {

            // Criando uma lista de respostas de sugestões vazia.
            List<ResponseSugestao> listaSugestoes = new ArrayList<ResponseSugestao>();

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
                listaSugestoes.add(new ResponseSugestao(sugestao));
            }

            // Sugestões que serão mostradas na página atual.
            // O início da página é a multiplicação do número da página pelo limite de sugestões por página.
            // O fim da página é o inicío da página mais o limite de sugestões por página.
            List<ResponseSugestao> sugestoesPagina = listaSugestoes.subList(pagina * limite, (pagina * limite) + limite);

            // Representa o total de páginas da pesquisa.
            // O total de páginas é calculado dividindo o total de sugestões pelo limite de sugestões por página.
            // O valor é arredondado para cima porque existe a possibilidade da ultima página não possuir o número limite de sugestões.
            Integer totalPaginas = (int) Math.ceil(listaSugestoes.size() / (double) limite);

            // Total de sugestões da pesquisa.
            // Se o tamanho for maior que Integer.MAX_VALUE, o valor é Integer.MAX_VALUE.
            Integer totalRegistros = listaSugestoes.size();

            // Para cada sugestão da resposta
            for(ResponseSugestao sugestao : sugestoesPagina) {
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

            // Prepara uma resposta em formato de página
            ResponsePagina responseSugestao = PaginaUtils.criarResposta(pagina, limite, totalRegistros, totalPaginas, sugestoesPagina);

            // Adiciona à resposta um link para a primeira página da listagem de sugestoes.
            responseSugestao.add(
                linkTo(
                    methodOn(MercadoController.class).ler(id, produtoId, 0, limite)
                )
                .withRel("first")
            );

            // Se a página de sugestões não estiver vazia.
            if(!listaSugestoes.isEmpty()) {
                // Se a página informada pelo cliente não for a primeira página da listagem de sugestões.
                if(pagina > 0) {
                    // Adiciona à resposta um link para a página anterior da listagem de sugestões.
                    responseSugestao.add(
                        linkTo(
                            methodOn(MercadoController.class).ler(id, produtoId, pagina - 1, limite)
                        )
                        .withRel("previous")
                    );
                }

                // Se a página informada pelo cliente não for a última página da listagem de sugestões.
                if(pagina < totalPaginas - 1) {
                    // Adiciona à resposta um link para a página seguinte da listagem de sugestões.
                    responseSugestao.add(
                        linkTo(
                            methodOn(MercadoController.class).ler(id, produtoId, pagina + 1, limite)
                        )
                        .withRel("next")
                    );
                }

                // Adiciona à resposta um link para a última página da listagem de sugestões.
                responseSugestao.add(
                    linkTo(
                        methodOn(MercadoController.class).ler(id, produtoId, totalPaginas - 1, limite)
                    )
                    .withRel("last")
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
     * Sobrecarga do método responsável por listar todas as sugestões de um produto específico no mercado. Pode ser ordenado.
     * @param id - Id do mercado que será buscado como parâmetro.
     * @param produtoId - Id do produto no qual será buscadas as sugestões.
     * @param ordenarPor - Campo do banco de dados que servirá de parâmetro para ordenar.
     * @param ordem - Direção em que os dados serão ordenados entre "asc" e "desc".
     * @return <b>List < ResponseSugestao ></b> - Lista de sugestões do produto específico no mercado ordenadas.
     */
    @GetMapping(value = "/{id}/produto/{produtoId}/sugestao", params = {"ordenarPor", "ordem"})
    @Cacheable("mercadoSugestoes")
    public List<ResponseSugestao> ler(
        @PathVariable("id") Integer id,
        @PathVariable(value = "produtoId") Integer produtoId,
        @RequestParam(required = false, defaultValue = "") String ordenarPor,
        @RequestParam(required = false, defaultValue = "asc") String ordem
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

            // Faz a ordenação da lista de sugestões.
            responseSugestao.sort((o1, o2) -> {

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
                    
                    case "preco":

                        // Se a ordem for "asc"
                        if(ordem.equals("asc"))
                            // Ordena a lista de produtos por preco ascendente.
                            return o1.getPreco().compareTo(o2.getPreco());
                        // Se a ordem for "desc"
                        else
                            // Ordena a lista de produtos por preco descendente.
                            return o2.getPreco().compareTo(o1.getPreco());
                    
                    case "timestamp":

                        // Se a ordem for "asc"
                        if(ordem.equals("asc"))
                            // Ordena a lista de produtos por data ascendente.
                            return o1.getTimestamp().compareTo(o2.getTimestamp());
                        // Se a ordem for "desc"
                        else
                            // Ordena a lista de produtos por data descendente.
                            return o2.getTimestamp().compareTo(o1.getTimestamp());
                    
                    case "estoqueId":

                        // Se a ordem for "asc"
                        if(ordem.equals("asc"))
                            // Ordena a lista de produtos por id do estoque ascendente.
                            return o1.getEstoqueId().compareTo(o2.getEstoqueId());
                        // Se a ordem for "desc"
                        else
                            // Ordena a lista de produtos por id do estoque descendente.
                            return o2.getEstoqueId().compareTo(o1.getEstoqueId());
                
                    default:
                        return 0;
                }
            });

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
     * Sobrecarga do método responsável por listar todas as sugestões de um produto específico no mercado. Pode ser paginado e ordenado.
     * @param id - Id do mercado que será buscado como parâmetro.
     * @param produtoId - Id do produto no qual será buscadas as sugestões.
     * @param pagina - Número da página que será listada.
     * @param limite - Número de registros que serão listados por página.
     * @param ordenarPor - Campo do banco de dados que servirá de parâmetro para ordenar.
     * @param ordem - Direção em que os dados serão ordenados entre "asc" e "desc".
     * @return <b>ResponsePagina</b> - Objeto contendo as informações da página e a lista de sugestões ordenada.
     */
    @GetMapping(value = "/{id}/produto/{produtoId}/sugestao", params = {"pagina", "limite", "ordenarPor", "ordem"})
    @Cacheable("mercadoSugestoes")
    public ResponsePagina ler(
        @PathVariable("id") Integer id,
        @PathVariable(value = "produtoId") Integer produtoId,
        @RequestParam(required = false, defaultValue = "0") Integer pagina,
        @RequestParam(required = false, defaultValue = "10") Integer limite,
        @RequestParam(required = false, defaultValue = "") String ordenarPor,
        @RequestParam(required = false, defaultValue = "asc") String ordem
    ) {
        try {

            // Criando uma lista de respostas de sugestões vazia.
            List<ResponseSugestao> listaSugestoes = new ArrayList<ResponseSugestao>();

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
                listaSugestoes.add(new ResponseSugestao(sugestao));
            }

            // Faz a ordenação da lista de sugestões.
            listaSugestoes.sort((o1, o2) -> {

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
                    
                    case "preco":

                        // Se a ordem for "asc"
                        if(ordem.equals("asc"))
                            // Ordena a lista de produtos por preco ascendente.
                            return o1.getPreco().compareTo(o2.getPreco());
                        // Se a ordem for "desc"
                        else
                            // Ordena a lista de produtos por preco descendente.
                            return o2.getPreco().compareTo(o1.getPreco());
                    
                    case "timestamp":

                        // Se a ordem for "asc"
                        if(ordem.equals("asc"))
                            // Ordena a lista de produtos por data ascendente.
                            return o1.getTimestamp().compareTo(o2.getTimestamp());
                        // Se a ordem for "desc"
                        else
                            // Ordena a lista de produtos por data descendente.
                            return o2.getTimestamp().compareTo(o1.getTimestamp());
                    
                    case "estoqueId":

                        // Se a ordem for "asc"
                        if(ordem.equals("asc"))
                            // Ordena a lista de produtos por id do estoque ascendente.
                            return o1.getEstoqueId().compareTo(o2.getEstoqueId());
                        // Se a ordem for "desc"
                        else
                            // Ordena a lista de produtos por id do estoque descendente.
                            return o2.getEstoqueId().compareTo(o1.getEstoqueId());
                
                    default:
                        return 0;
                }
            });

            // Sugestões que serão mostradas na página atual.
            // O início da página é a multiplicação do número da página pelo limite de sugestões por página.
            // O fim da página é o inicío da página mais o limite de sugestões por página.
            List<ResponseSugestao> sugestoesPagina = listaSugestoes.subList(pagina * limite, (pagina * limite) + limite);

            // Representa o total de páginas da pesquisa.
            // O total de páginas é calculado dividindo o total de sugestões pelo limite de sugestões por página.
            // O valor é arredondado para cima porque existe a possibilidade da ultima página não possuir o número limite de sugestões.
            Integer totalPaginas = (int) Math.ceil(listaSugestoes.size() / (double) limite);

            // Total de sugestões da pesquisa.
            // Se o tamanho for maior que Integer.MAX_VALUE, o valor é Integer.MAX_VALUE.
            Integer totalRegistros = listaSugestoes.size();

            // Para cada sugestão da resposta
            for(ResponseSugestao sugestao : sugestoesPagina) {
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

            // Prepara uma resposta em formato de página
            ResponsePagina responseSugestao = PaginaUtils.criarResposta(pagina, limite, totalRegistros, totalPaginas, sugestoesPagina);

            // Adiciona à resposta um link para a primeira página da listagem de sugestoes.
            responseSugestao.add(
                linkTo(
                    methodOn(MercadoController.class).ler(id, produtoId, 0, limite)
                )
                .withRel("first")
            );

            // Se a página de sugestões não estiver vazia.
            if(!listaSugestoes.isEmpty()) {
                // Se a página informada pelo cliente não for a primeira página da listagem de sugestões.
                if(pagina > 0) {
                    // Adiciona à resposta um link para a página anterior da listagem de sugestões.
                    responseSugestao.add(
                        linkTo(
                            methodOn(MercadoController.class).ler(id, produtoId, pagina - 1, limite)
                        )
                        .withRel("previous")
                    );
                }

                // Se a página informada pelo cliente não for a última página da listagem de sugestões.
                if(pagina < totalPaginas - 1) {
                    // Adiciona à resposta um link para a página seguinte da listagem de sugestões.
                    responseSugestao.add(
                        linkTo(
                            methodOn(MercadoController.class).ler(id, produtoId, pagina + 1, limite)
                        )
                        .withRel("next")
                    );
                }

                // Adiciona à resposta um link para a última página da listagem de sugestões.
                responseSugestao.add(
                    linkTo(
                        methodOn(MercadoController.class).ler(id, produtoId, totalPaginas - 1, limite)
                    )
                    .withRel("last")
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
    @Cacheable("mercados")
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
    @Cacheable("mercados")
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
     * Sobrecarga do método responsável por listar todos os mercados podendo ser ordenado.
     * @param mercado - Objeto do tipo Mercado que será usado como parâmetro para filtrar os resultados.
     * @param ordenarPor - Campo do banco de dados que servirá de parâmetro para ordenar.
     * @param ordem - Direção em que os dados serão ordenados entre "asc" e "desc".
     * @return <b>List < ResponseMercado ></b> - Lista de mercados ordenados.
     */
    @GetMapping(params = { "ordenarPor", "ordem" })
    @Cacheable("mercados")
    public List<ResponseMercado> listar(
        Mercado requestMercado,
        @RequestParam(required = false, defaultValue = "") String ordenarPor,
        @RequestParam(required = false, defaultValue = "asc") String ordem
    ) {
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
                ),
                Sort.by(Sort.Direction.fromString(ordem), ordenarPor)
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
     * Sobrecarga do método responsável por listar todos os mercados podendo ser ordenado e paginado.
     * @param mercado - Objeto do tipo Mercado que será usado como parâmetro para filtrar os resultados.
     * @param ordenarPor - Campo do banco de dados que servirá de parâmetro para ordenar.
     * @param ordem - Direção em que os dados serão ordenados entre "asc" e "desc".
     * @param pagina - Número da página que será exibida, contada a partir do 0.
     * @param limite - Número de registros que serão exibidos por página.
     * @return <b>ResponsePagina</b> - Lista de mercados ordenados com as infromações de paginação.
     */
    @GetMapping(params = { "ordenarPor", "ordem", "pagina", "limite" })
    //@Cacheable("mercados")
    public ResponsePagina listar(
        Mercado requestMercado,
        @RequestParam(required = false, defaultValue = "") String ordenarPor,
        @RequestParam(required = false, defaultValue = "asc") String ordem,
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
                PageRequest.of(pagina, limite, Sort.by(Sort.Direction.fromString(ordem), ordenarPor))
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
    @CacheEvict(value = "mercados", allEntries = true)
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
