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
 * Classe respons??vel por controlar as requisi????es de produtos.
 */
@RestController
@RequestMapping("/produto")
public class ProdutoController {

    // Reposit??rios respons??vel pelos m??todos JPA dp banco de dados.
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
     * Rota respons??vel por criar um produto.
     * @param categoria - Dados do produto a ser criado.
     * @return Dados e id do produto criado.
     */
    @PostMapping
    public ResponseProduto criar(@RequestBody Produto requestProduto) {
        try {

            // Valida os dados fornecitos.
            Tratamento.validarProduto(requestProduto, false);

            // Se o usu??rio fornecido n??o existir,
            if(!usuarioRepository.existsById(requestProduto.getCriadoPor()))
                // Retorna um erro.
                throw new DadosInvalidosException("usuario_nao_encontrado");
            
            // Se a categoria fornecida n??o existir,
            if(!categoriaRepository.existsById(requestProduto.getCategoriaId()))
                // Retorna um erro.
                throw new DadosInvalidosException("categoria_nao_encontrado");

            // Se as caracter??sticas fornecidas pertencer a um produto j?? existente,
            if(produtoRepository.findByCaracteristicas(
                requestProduto.getNome(), 
                requestProduto.getMarca(),
                requestProduto.getTamanho(), 
                requestProduto.getCor()) != null
            )
                // Retorna um erro.
                throw new DadosConflitantesException("produto_existente");

            // Buscando o usu??rio informado como criador do produto.
            Usuario usuario = usuarioRepository.findById(requestProduto.getCriadoPor()).get();

            // Verifica se o usu??rio informado como criador n??o foi deletado anteriormente.
            if(!Tratamento.usuarioExiste(usuario))
                // Retorna um erro.
                throw new NoSuchElementException("usuario_nao_encontrado");

            // Criando uma nova inst??ncia do produto para tratar o nome dele e cri??-lo no banco.
            Produto produtoTratado = requestProduto;

            // Pegando cada palavra do nome do produto em min??sculas separado por espa??o.
            String[] nomeProduto = requestProduto.getNome().toLowerCase().split(" ");

            // Percorrendo cada palavra do nome do produto
            for(int i = 0; i < nomeProduto.length; i++) {
                // Transformando a palavra atual em uma array
                char[] palavraArr = nomeProduto[i].toCharArray();

                // Transformando a primeira letra da palavra em mai??scula
                palavraArr[0] = nomeProduto[i].toUpperCase().charAt(0);

                // Reescreve a palavra atual com a primeira letra tratada
                nomeProduto[i] = String.valueOf(palavraArr);

                // Se for a primeira palavra sendo tratada,
                // substitui o nome do produto pelo nome tratado
                if(i < 1)
                    produtoTratado.setNome(nomeProduto[i]);
                // Se n??o, concatena a palavra atual ao nome do produto
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
     * Rota respons??vel por ler um produto.
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
     * Rota respons??vel por listar todos os mercados onde o produto pode ser encontrado.
     * @param id - Id do produto a ser buscado como par??metro.
     * @return <b>List< ResponseMercado ></b> - Lista de mercados onde o produto pode ser encontrado.
     */
    @GetMapping("/{id}/mercado")
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
                            // Passando como par??metro o ID do mercado no estoque atual.
                            estoque.getMercadoId()
                        )
                        .get()
                    )
                );
            }

            // Se n??o houver nenhum mercado que possua o produto
            if(responseMercado.isEmpty())
                // Lan??a uma exce????o informando que n??o foi encontrado um estoque com o produto informado.
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

                // Adiciona ao mercado o link para a rota de listagem de mercados (cole????o).
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
     * Rota respons??vel pelo levanamento de pre??os de um produto.
     * @param id - Id do produto.
     * @return Informa????es do levantamento e lista de pre??os.
     */
    @GetMapping("/{id}/levantamento")
    public ResponseLevantamentoProduto levantamento(@PathVariable("id") Integer id) {
        try {

            // Busca o produto no banco e transforma os dados obtidos em modelo de resposta.
            ResponseLevantamentoProduto responseProduto = new ResponseLevantamentoProduto(produtoRepository.findById(id).get());

            // Busca os estoques do produto.
            List<Estoque> estoques = estoqueRepository.findByProdutoId(id);

            float somaPreco = 0.0f; // Soma do pre??o encontrado em cada mercado.
            int quantidadeSugestoes = 0; // Quantidade total de sugest??es encontradas.

            // Se houver estoques desse produto,
            if(estoques != null) {
                // Percorre os estoques do produto.
                for(Estoque estoque : estoques) {
                    // Buscando as sugest??es do produto no mercado atual
                    List<Sugestao> sugestoes = sugestaoRepository.findByEstoqueId(estoque.getId());

                    // Se houver sugest??es do produto no mercado atual,
                    if(sugestoes != null) {
                        // Percorre as sugest??es do produto,
                        for(Sugestao sugestao : sugestoes) {
                            // Incrementa a quantidade de sugest??es encontradas.
                            quantidadeSugestoes++;
                            // Soma o pre??o da sugest??o.
                            somaPreco += (sugestao.getPreco() / 100);

                            // Se n??o houver data da ultima sugest??o,
                            if(responseProduto.getDataUltimaSugestao() == null)
                                // Define a data do momento
                                responseProduto.setDataUltimaSugestao(sugestao.getTimestamp());
                            // Ou se a data da ultima sugest??o for antes da data do momento,
                            else if(responseProduto.getDataUltimaSugestao().before(sugestao.getTimestamp()))
                                // Define a data do momento
                                responseProduto.setDataUltimaSugestao(sugestao.getTimestamp());

                            // Se o maior pre??o encontrado for 0,
                            if(responseProduto.getMaiorPreco() == 0.0f)
                                // Define o maior pre??o encontrado como o pre??o da sugest??o atual
                                responseProduto.setMaiorPreco(sugestao.getPreco() / 100);
                            // Ou se o pre??o da sugest??o atual for maior que o maior pre??o encontrado,
                            else if(responseProduto.getMaiorPreco() < sugestao.getPreco())
                                // Define o maior pre??o encontrado como o pre??o da sugest??o atual
                                responseProduto.setMaiorPreco(sugestao.getPreco() / 100);

                            // Se o menor pre??o encontrado for 0,
                            if(responseProduto.getMenorPreco() == 0.0f)
                                // Define o menor pre??o encontrado como o pre??o da sugest??o atual
                                responseProduto.setMenorPreco(sugestao.getPreco() / 100);
                            // Ou se o pre??o da sugest??o atual for maior que o menor pre??o encontrado,
                            else if(responseProduto.getMenorPreco() > sugestao.getPreco())
                                // Define o menor pre??o encontrado como o pre??o da sugest??o atual
                                responseProduto.setMenorPreco(sugestao.getPreco() / 100);
                        }
                    }
                }
            }

            // Define a quantidade de sugest??es encontradas.
            responseProduto.setQuantidadeSugestoes(quantidadeSugestoes);
            // Define o pre??o m??dio do produto.
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
     * Rota respons??vel por listar todos os produtos.
     * @param requestProduto - Dados de pesquisa para filtragem.
     * @return Lista de produtos.
     */
    @GetMapping
    public List<ResponseProduto> listar(Produto requestProduto) {
        try {

            // Valida os dados de pesquisa.
            Tratamento.validarProduto(requestProduto, true);

            // Busca os produtos no banco semelhantes aos dados de pesquisa.
            // Se n??o houver dados de pesquisa, busca todos os produtos.
            List<Produto> produtos = produtoRepository.findAll(
                Example.of(
                    requestProduto, 
                    ExampleMatcher
                        .matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                )
            );

            // Lista de produtos que ser?? retornada.
            List<ResponseProduto> responseProduto = new ArrayList<ResponseProduto>();

            // Adiciona os prdutos ?? lista.
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
     * Rota respons??vel por listar produtos.
     * @param requestProduto - Dados de pesquisa para filtragem.
     * @param pagina - N??mero da p??gina a ser mostrada.
     * @param limite - N??mero de registros por p??gina.
     * @return Lista de produtos com os dados da p??gina.
     */
    @GetMapping(params = { "pagina", "limite" })
    public ResponsePagina listar(
        Produto requestProduto,
        @RequestParam(required = false, defaultValue = "0") Integer pagina,
        @RequestParam(required = false, defaultValue = "10") Integer limite
    ) {
        try {

            // Valida os dados de pesquisa.
            Tratamento.validarProduto(requestProduto, true);

            // Busca os produtos no banco semelhantes aos dados de pesquisa.
            // Se n??o houver dados de pesquisa, busca todos os produtos.
            // Informa os dados de pagina????o.
            // Se n??o houver pagina????o, busca todos os produtos.
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

            // Lista de produtos que ser?? retornada.
            List<ResponseProduto> produtos = new ArrayList<ResponseProduto>();

            // Formata a resposta com os dados obtidos.
            ResponsePagina responseProduto = PaginaUtils.criarResposta(pagina, limite, paginaProduto, produtos);

            // Adiciona os prdutos ?? lista.
            for(Produto produto : paginaProduto.getContent()) {
                produtos.add(new ResponseProduto(produto));
            }

            // Adiciona um link para a primeira p??gina de produtos.
            responseProduto.add(
                linkTo(
                    methodOn(ProdutoController.class).listar(requestProduto, 0, limite)
                )
                .withRel("first")
            );

            // Se houver produtos,
            if(!paginaProduto.isEmpty()) {
                // Se a p??gina atual n??o for a primeira,
                if(pagina > 0) {
                    // Adiciona um link para a p??gina anterior de produtos.
                    responseProduto.add(
                        linkTo(
                            methodOn(ProdutoController.class).listar(requestProduto, pagina - 1, limite)
                        )
                        .withRel("previous")
                    );
                }
                
                // Se a p??gina atual n??o for a ??ltima,
                if(pagina < paginaProduto.getTotalPages() - 1) {
                    // Adiciona um link para a p??gina seguinte de produtos.
                    responseProduto.add(
                        linkTo(
                            methodOn(ProdutoController.class).listar(requestProduto, pagina + 1, limite)
                        )
                        .withRel("next")
                    );
                }

                // Adiciona um link para a ??ltima p??gina de produtos.
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
     * Rota respons??vel por editar um produto.
     * @param id - Id do produto a ser editado.
     * @param requestProduto - Dados modificados do produto.
     * @return Dados novos do produto editado.
     */
    @PatchMapping("/{id}")
    public ResponseProduto editar(@PathVariable int id, @RequestBody Produto requestProduto) {
        try {

            // Valida os dados fornecidos.
            Tratamento.validarProduto(requestProduto, true);

            // Se as caracter??sticas fornecidas pertencer a um produto j?? existente,
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
            
            // Se a categoria informada n??o existir,
            if(!categoriaRepository.existsById(requestProduto.getCategoriaId()))
                // Retorna um erro.
                throw new DadosInvalidosException("categoria_nao_encontrado");

            // Chama o recurso de tratamento de edi????o de produto.
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
     * Rota respons??vel por substituir um produto.
     * @param id - Id do produto a ser substitu??do.
     * @param requestProduto - Dados do novo produto.
     * @return Dados do novo produto.
     */
    @PutMapping("/{id}")
    public ResponseProduto atualizar(@PathVariable int id, @RequestBody Produto requestProduto) {
        try {

            // Valida os dados fornecidos.
            Tratamento.validarProduto(requestProduto, false);

            // Se o usu??rio informado n??o existir,
            if(!usuarioRepository.existsById(requestProduto.getCriadoPor()))
                // Retorna um erro.
                throw new DadosInvalidosException("usuario_nao_encontrado");

            // Se a categoria informada n??o existir,
            if(!categoriaRepository.existsById(requestProduto.getCategoriaId()))
                // Retorna um erro.
                throw new DadosInvalidosException("categoria_nao_encontrado");

            // Se as caracter??sticas fornecidas pertencer a um produto j?? existente,
            if(produtoRepository.findByCaracteristicas(
                requestProduto.getNome(), 
                requestProduto.getMarca(),
                requestProduto.getTamanho(), 
                requestProduto.getCor()) != null
            )
                // Retorna um erro.
                throw new DadosConflitantesException("produto_existente");

            // Busca o produto a ser substitu??do.
            Produto produto = produtoRepository.findById(id).get();

            // Define o id do produto como o id fornecido.
            requestProduto.setId(id);

            // Define o id do usu??rio criador como o id atual do banco dedos, haja vista que n??o pode ser alterado.
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
     * Rota respons??vel por excluir um produto.
     * @param id - Id do produto a ser exclu??do.
     */
    @DeleteMapping("/{id}")
    public Object remover(@PathVariable int id) {
        try {

            // Se o produto informado n??o existir,
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
