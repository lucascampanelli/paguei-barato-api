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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.pagueibaratoapi.models.exceptions.DadosConflitantesException;
import com.pagueibaratoapi.models.exceptions.DadosInvalidosException;
import com.pagueibaratoapi.models.requests.Estoque;
import com.pagueibaratoapi.models.requests.Usuario;
import com.pagueibaratoapi.models.responses.ResponseEstoque;
import com.pagueibaratoapi.models.responses.ResponsePagina;
import com.pagueibaratoapi.repository.EstoqueRepository;
import com.pagueibaratoapi.repository.MercadoRepository;
import com.pagueibaratoapi.repository.ProdutoRepository;
import com.pagueibaratoapi.repository.UsuarioRepository;
import com.pagueibaratoapi.utils.PaginaUtils;
import com.pagueibaratoapi.utils.Tratamento;
import com.pagueibaratoapi.utils.tratamentos.TratamentoEstoque;

/**
 * Classe responsável por controlar as requisições de estoque.
 */
@RestController
@RequestMapping("/estoque")
public class EstoqueController {

    // Iniciando as variáveis de instância dos repositórios.
    private final EstoqueRepository estoqueRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;
    private final MercadoRepository mercadoRepository;

    // Construtor do controller do estoque que realiza a injeção de dependência dos repositórios.
    public EstoqueController(
        EstoqueRepository estoqueRepository,
        UsuarioRepository usuarioRepository,
        ProdutoRepository produtoRepository,
        MercadoRepository mercadoRepository
    ) {
        this.estoqueRepository = estoqueRepository;
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
        this.mercadoRepository = mercadoRepository;
    }

    /**
     * Método responsável por criar um novo item no estoque.
     * @param estoque Objeto do tipo estoque que será criado.
     * @return Objeto do tipo estoque que foi criado.
     * @throws DadosInvalidosException Lança exceção caso os dados do estoque sejam inválidos.
     * @throws DadosConflitantesException Lança exceção caso os dados do estoque sejam conflitantes.
     */
    @PostMapping
    @CacheEvict(value = "Estoques", allEntries = true)
    public ResponseEstoque criar(@RequestBody Estoque requestEstoque) {
        try {

            // Validando os dados enviados pelo usuário.
            TratamentoEstoque.validar(requestEstoque, false);

            // Verifica se o usuário informado existe. Caso não exista, lança exceção.
            if(!usuarioRepository.existsById(requestEstoque.getCriadoPor()))
                throw new DadosInvalidosException("usuario_invalido");

            // Obtendo o usuário informado.
            Usuario usuario = usuarioRepository.findById(requestEstoque.getCriadoPor()).get();

            // Verifica se o usuário informado não foi deletado. Caso tenha sido, lança exceção.
            if(!Tratamento.usuarioExiste(usuario))
                throw new DadosInvalidosException("usuario_invalido");

            // Verifica se o produto informado existe. Caso não exista, lança exceção.
            if(!produtoRepository.existsById(requestEstoque.getProdutoId()))
                throw new DadosInvalidosException("produto_invalido");

            // Verifica se o mercado informado existe. Caso não exista, lança exceção.
            if(!mercadoRepository.existsById(requestEstoque.getMercadoId()))
                throw new DadosInvalidosException("mercado_invalido");

            // Instanciando um objeto do estoque que será utilizado para verificar se o estoque já existe.
            Estoque estoqueComparar = new Estoque();
            // Definindo o id do produto informado pelo usuário.
            estoqueComparar.setProdutoId(requestEstoque.getProdutoId());
            // Definindo o id do mercado informado pelo usuário.
            estoqueComparar.setMercadoId(requestEstoque.getMercadoId());

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

            // Criando um novo estoque.
            ResponseEstoque responseEstoque = new ResponseEstoque(estoqueRepository.save(requestEstoque));

            // Adicionando um link para ler o estoque criado.
            responseEstoque.add(
                linkTo(
                    methodOn(EstoqueController.class).ler(responseEstoque.getId())
                )
                .withSelfRel()
            );

            // Retornando o estoque criado.
            return responseEstoque;

        } catch (DadosConflitantesException e) {
            // Lançando exceção caso os dados do estoque sejam conflitantes.
            // Retorna um status 409 com a mensagem de erro do conflito.
            throw new ResponseStatusException(409, e.getMessage(), e);
        } catch (DadosInvalidosException e) {
            // Lançando exceção caso os dados do estoque sejam inválidos.
            // Retorna um status 400 com a mensagem de erro da requisição inválida.
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (DataIntegrityViolationException e) {
            // Lançando exceção caso os dados do estoque sejam inválidos.
            // Retorna um status 500 com a mensagem de erro da integridade de dados.
            throw new ResponseStatusException(500, "erro_insercao", e);
        } catch (IllegalArgumentException e) {
            // Lançando exceção caso os dados do estoque enviado sejam inválidos.
            // Retorna um status 500 com a mensagem de erro.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (Exception e) {
            // Lançando exceção caso ocorra algum erro inesperado.
            // Retorna um status 500 com a mensagem de erro.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Método responsável por ler um item do estoque.
     * @param id - Id do item do estoque que será lido.
     * @return ResponseEstoque - Objeto do estoque que foi lido.
     */
    @GetMapping("/{id}")
    public ResponseEstoque ler(@PathVariable("id") Integer id) {
        try {
            // Buscando o estoque pelo id informado e armazenando numa instância do tipo ResponseEstoque.
            ResponseEstoque responseEstoque = new ResponseEstoque(estoqueRepository.findById(id).get());

            // Se houver um estoque com o id informado, adiciona um link para listar todos os estoques.
            if(responseEstoque != null) {
                responseEstoque.add(
                    linkTo(
                        methodOn(EstoqueController.class).listar(new Estoque())
                    )
                    .withRel("collection")
                );
            }

            // Retornando o estoque lido.
            return responseEstoque;

        } catch (NoSuchElementException e) {
            // Lançando exceção caso o estoque não exista.
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (Exception e) {
            // Lançando exceção caso ocorra algum erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Método responsável por listar todos os itens do estoque.
     * @param requestEstoque - Objeto do tipo Estoque que será utilizado para filtrar os itens do estoque.
     * @return List< ResponseEstoque > - Lista de objetos do tipo ResponseEstoque que representam os itens do estoque.
     */
    @GetMapping
    @Cacheable("Estoques")
    public List<ResponseEstoque> listar(Estoque requestEstoque) {
        try {
            // Validando o estoque enviado como parâmetro
            Tratamento.validarEstoque(requestEstoque, true);

            // Buscando todos os estoques que atendam aos filtros informados e armazenando numa lista de estoques.
            List<Estoque> estoques = estoqueRepository.findAll(
                Example.of(
                    requestEstoque, 
                    ExampleMatcher
                        .matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                )
            );

            // Criando uma lista de objetos do tipo ResponseEstoque.
            List<ResponseEstoque> responseEstoque = new ArrayList<ResponseEstoque>();

            // Para cada estoque encontrado, cria um objeto do tipo ResponseEstoque e adiciona na lista de resposta.
            // Etapa realizada para que o Estoque seja convertido em um objeto do tipo ResponseEstoque.
            for(Estoque estoque : estoques) {
                responseEstoque.add(new ResponseEstoque(estoque));
            }

            // Se houver estoques
            if(!responseEstoque.isEmpty()) {

                // Para cada estoque da lista de retorno
                for(ResponseEstoque estoque : responseEstoque) {

                    // Adiciona um link para ler o estoque em questão
                    estoque.add(
                        linkTo(
                            methodOn(EstoqueController.class).ler(estoque.getId())
                        )
                        .withSelfRel()
                    );

                }

            }

            return responseEstoque;

        } catch (DadosInvalidosException e) {
            // Lançando exceção caso os dados do estoque sejam inválidos.
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (NullPointerException e) {
            // Lançando exceção caso o estoque do parâmetro não exista.
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (UnsupportedOperationException e) {
            // Lançando exceção caso aconteça algum erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (Exception e) {
            // Lançando exceção caso aconteça algum erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Método responsável por listar todos os itens do estoque com paginação.
     * @param requestEstoque - Objeto do tipo Estoque que será utilizado para filtrar os itens do estoque.
     * @param pagina - Número da página que será exibida, sendo contada a partir do 0.
     * @param limite - Número de itens que serão exibidos por página.
     * @return ResponseEstoque - Objeto com os estoques retornados e as informações da paginação;
     */
    @GetMapping(params = { "pagina", "limite" })
    @Cacheable("Estoques")
    public ResponsePagina listar(
        Estoque requestEstoque,
        @RequestParam(required = false, defaultValue = "0") Integer pagina, 
        @RequestParam(required = false, defaultValue = "10") Integer limite
    ) {
        try {
            // Validando o estoque enviado como parâmetro.
            Tratamento.validarEstoque(requestEstoque, true);

            // Buscando todos os estoques que atendam aos filtros informados e armazenando numa página de estoques.
            Page<Estoque> paginaEstoque = estoqueRepository.findAll(
                Example.of(
                    requestEstoque, 
                    ExampleMatcher
                        .matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                ),
                PageRequest.of(pagina, limite)
            );

            // Criando uma lista de objetos do tipo ResponseEstoque.
            List<ResponseEstoque> estoques = new ArrayList<ResponseEstoque>();

            // Para cada estoque da página de estoques
            for(Estoque estoque : paginaEstoque.getContent()) {
                // Cria um objeto do tipo ResponseEstoque e adiciona na lista de resposta.
                estoques.add(new ResponseEstoque(estoque));
            }

            // Criando um objeto do tipo ResponsePagina, que será o retorno com os estoques e as informações da página.
            ResponsePagina responseEstoque = PaginaUtils.criarResposta(pagina, limite, paginaEstoque, estoques);

            // Adiciona à resposta um link para listar a primeira página.
            responseEstoque.add(
                linkTo(
                    methodOn(EstoqueController.class).listar(requestEstoque, 0, limite)
                )
                .withRel("first")
            );

            // Se a resposta não estiver vazia.
            if(!paginaEstoque.isEmpty()) {
                // Se a página enviada pelo cliente for maior que 0.
                if(pagina > 0) {
                    // Adiciona à resposta um link para listar a página anterior.
                    responseEstoque.add(
                        linkTo(
                            methodOn(EstoqueController.class).listar(requestEstoque, pagina - 1, limite)
                        )
                        .withRel("previous")
                    );
                }

                // Se a página enviada pelo cliente for menor que o número de páginas do estoque.
                if(pagina < paginaEstoque.getTotalPages() - 1) {
                    // Adiciona à resposta um link para listar a página posterior.
                    responseEstoque.add(
                        linkTo(
                            methodOn(EstoqueController.class).listar(requestEstoque, pagina + 1, limite)
                        )
                        .withRel("next")
                    );
                }

                // Adiciona à resposta um link para listar a última página.
                responseEstoque.add(
                    linkTo(
                        methodOn(EstoqueController.class).listar(requestEstoque, paginaEstoque.getTotalPages() - 1, limite)
                    )
                    .withRel("last")
                );

                // Para cada estoque da lista de retorno.
                for(ResponseEstoque estoque : estoques) {
                    // Adiciona um link para ler o estoque em questão.
                    estoque.add(
                        linkTo(
                            methodOn(EstoqueController.class).ler(estoque.getId())
                        )
                        .withSelfRel()
                    );
                }
            }

            // Retorna a lista com os estoques e as informações da página.
            return responseEstoque;

        } catch (DadosInvalidosException e) {
            // Lançando exceção caso os dados do estoque de parâmetro sejam inválidos.
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            // Lançando exceção caso o parâmetro enviado seja inválido.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (NullPointerException e) {
            // Lançando exceção caso o estoque do parâmetro não exista.
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (Exception e) {
            // Lançando exceção caso aconteça algum erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Método responsável por deletar um estoque com o id informado.
     * @param id Id do estoque que será deletado.
     * @return Object - Link para listar os estoques.
     */
    @DeleteMapping("/{id}")
    @CacheEvict(value = "Estoques", allEntries = true)
    public Object remover(@PathVariable int id) {
        try {

            // Se o estoque com o id informado não existir.
            if(!estoqueRepository.existsById(id))
                // Lança uma exceção indicando que o estoque não existe.
                throw new NoSuchElementException("nao_encontrado");

            // Deleta o estoque com o id informado.
            estoqueRepository.deleteById(id);

            // Retorna um link para listar todos os estoques.
            return linkTo(
                        methodOn(EstoqueController.class).listar(new Estoque())
                    )
                    .withRel("collection");

        } catch (NoSuchElementException e) {
            // Lança uma exceção indicando que o estoque não existe.
            throw new ResponseStatusException(404, e.getMessage(), e);
        } catch (DataIntegrityViolationException e) {
            // Lança uma exceção indicando que o estoque não pode ser deletado.
            throw new ResponseStatusException(500, "erro_remocao", e);
        } catch (IllegalArgumentException e) {
            // Lança uma exceção indicando que o parâmetro enviado é inválido
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (Exception e) {
            // Lança uma exceção indicando que aconteceu algum erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }
}
