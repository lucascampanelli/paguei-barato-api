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
import com.pagueibaratoapi.models.requests.Sugestao;
import com.pagueibaratoapi.models.requests.Usuario;
import com.pagueibaratoapi.models.responses.ResponseMercado;
import com.pagueibaratoapi.models.responses.ResponsePagina;
import com.pagueibaratoapi.models.responses.ResponseSugestao;
import com.pagueibaratoapi.repository.EstoqueRepository;
import com.pagueibaratoapi.repository.MercadoRepository;
import com.pagueibaratoapi.repository.RamoRepository;
import com.pagueibaratoapi.repository.SugestaoRepository;
import com.pagueibaratoapi.repository.UsuarioRepository;
import com.pagueibaratoapi.utils.EditaRecurso;
import com.pagueibaratoapi.utils.PaginaUtils;
import com.pagueibaratoapi.utils.Tratamento;

/**
* Classe respons??vel por controlar as requisi????es de Mercado.
*/
@RestController
@RequestMapping("/mercado")
public class MercadoController {

    // Iniciando as vari??veis de inst??ncia dos reposit??rios.
    private final MercadoRepository mercadoRepository;
    private final RamoRepository ramoRepository;
    private final UsuarioRepository usuarioRepository;
    private final SugestaoRepository sugestaoRepository;
    private final EstoqueRepository estoqueRepository;

    // Construtor do controller do estoque, que realiza a inje????o de depend??ncia dos reposit??rios.
    public MercadoController(
        MercadoRepository mercadoRepository,
        RamoRepository ramoRepository,
        UsuarioRepository usuarioRepository,
        SugestaoRepository sugestaoRepository,
        EstoqueRepository estoqueRepository
    ) {
        this.mercadoRepository = mercadoRepository;
        this.ramoRepository = ramoRepository;
        this.usuarioRepository = usuarioRepository;
        this.sugestaoRepository = sugestaoRepository;
        this.estoqueRepository = estoqueRepository;
    }

    /**
     * M??todo respons??vel por criar um novo mercado.
     * @param requestMercado - Objeto do tipo Mercado que cont??m os dados do novo mercado.
     * @return ResponseMercado - Objeto do tipo ResponseMercado que cont??m os dados do novo mercado, j?? com o Id criado.
     */
    @PostMapping
    public ResponseMercado criar(@RequestBody Mercado requestMercado) {
        try {

            // Validando os dados do mercado enviados pelo cliente.
            Tratamento.validarMercado(requestMercado, false);

            // Verificando se o usu??rio informado existe.
            if(!usuarioRepository.existsById(requestMercado.getCriadoPor()))
                // Se n??o existir, lan??a exce????o com mensagem de erro.
                throw new DadosInvalidosException("usuario_nao_encontrado");

            Usuario usuario = usuarioRepository.findById(requestMercado.getCriadoPor()).get();

            // Verificando se o usu??rio informado n??o foi deletado.
            if(!Tratamento.usuarioExiste(usuario))
                // Se o usu??rio foi deletado, lan??a exce????o com mensagem de erro.
                throw new NoSuchElementException("usuario_nao_encontrado");

            // Verificando se o ramo informado existe.
            if(!ramoRepository.existsById(requestMercado.getRamoId()))
                // Se n??o existirm, lan??a exce????o com mensagem de erro.
                throw new DadosInvalidosException("ramo_nao_encontrado");

            // Verificando se o nome do mercado informado j?? existe
            if(mercadoRepository.existsByNomeIgnoreCase(requestMercado.getNome()))
                throw new DadosConflitantesException("mercado_existente");

            // Verificando se o endere??o do mercado informado j?? existe
            if(mercadoRepository.findByEndereco(
                requestMercado.getLogradouro(), 
                requestMercado.getNumero(),
                requestMercado.getComplemento(), 
                requestMercado.getBairro(), 
                requestMercado.getCidade(),
                requestMercado.getUf(), 
                requestMercado.getCep()) != null
            )
                // Se j?? existir, lan??a exce????o com mensagem de erro, 
                // haja vista que n??o pode haver dois mercados no mesmo local.
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
            // Lan??a uma exce????o informando que os dados est??o em conflito com outros dados no banco de dados.
            throw new ResponseStatusException(409, e.getMessage(), e);
        } catch(NoSuchElementException e) {
            // Lan??a uma exce????o informando que algum registro informado n??o existe.
            throw new ResponseStatusException(404, e.getMessage(), e);
        } catch (DadosInvalidosException e) {
            // Lan??a uma exce????o informando que os dados enviados pelo cliente s??o inv??lidos.
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (DataIntegrityViolationException e) {
            // Lan??a uma exce????o informando que os dados enviados pelo cliente s??o inv??lidos.
            throw new ResponseStatusException(500, "erro_insercao", e);
        } catch (IllegalArgumentException e) {
            // Lan??a uma exce????o informando que os dados enviados pelo cliente s??o inv??lidos.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (Exception e) {
            // Lan??a uma exce????o informando que ocorreu um erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * M??todo respons??vel por ler um mercado com o id informado.
     * @param id - Id do mercado que ser?? lido.
     * @return ResponseMercado - Objeto do tipo ResponseMercado que cont??m os dados do mercado lido.
     */
    @GetMapping("/{id}")
    public ResponseMercado ler(@PathVariable("id") Integer id) {
        try {
            // Busca o mercado com o id informado e armazena num objeto do tipo ResponseMercado.
            ResponseMercado responseMercado = new ResponseMercado(mercadoRepository.findById(id).get());

            // Se houver um mercado com o id informado
            if(responseMercado != null) {

                // Adiciona ?? resposta um link para listar todos os mercados
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
            // Lan??a uma exce????o informando que o mercado com o id informado n??o foi encontrado.
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (Exception e) {
            // Lan??a uma exce????o informando que ocorreu um erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * M??todo respons??vel por listar todas as sugest??es de um produto espec??fico no mercado.
     * @param id - Id do mercado que ser?? buscado como par??metro.
     * @param produtoId - Id do produto no qual ser?? buscadas as sugest??es.
     * @return <b>List < ResponseSugestao ></b> - Lista de sugest??es do produto espec??fico no mercado.
     */
    @GetMapping("/{id}/produto/{produtoId}/sugestao")
    public List<ResponseSugestao> ler(
        @PathVariable("id") Integer id,
        @PathVariable(value = "produtoId") Integer produtoId
    ) {
        try {

            // Criando uma lista de respostas de sugest??es vazia.
            List<ResponseSugestao> responseSugestao = new ArrayList<ResponseSugestao>();

            // Buscando o registro do estoque do mercado, que associa o produto ao estoque
            // do mercado
            Estoque estoque = estoqueRepository.findByProdutoIdAndMercadoId(produtoId, id);

            // Se o estoque for nulo
            if(estoque == null)
                // Lan??a uma exce????o informando que o produto n??o est?? no estoque do mercado.
                throw new NoSuchElementException("estoque_nao_encontrado");

            // Buscando todas as sugest??es de pre??o do produto no mercado informado
            List<Sugestao> sugestoes = sugestaoRepository.findByEstoqueId(estoque.getId());

            // Se n??o houver sugest??es para esse produto
            if(sugestoes == null)
                // Lan??a uma exce????o informando que n??o h?? sugest??es para esse produto.
                throw new NoSuchElementException("sugestao_nao_encontrado");

            // Para cada sugest??o encontrada
            for(Sugestao sugestao : sugestoes) {
                // Cria um objeto do tipo ResponseSugestao, convertendo o objeto Sugestao para o objeto ResponseSugestao
                responseSugestao.add(new ResponseSugestao(sugestao));
            }

            // Para cada sugest??o da resposta
            for(ResponseSugestao sugestao : responseSugestao) {
                // Converte o pre??o da sugest??o de int para float para ser exibido no formato de moeda no retorno.
                sugestao.setPreco(sugestao.getPreco() / 100);

                // Adiciona ?? resposta um link para a leitura da sugest??o em quest??o.
                sugestao.add(
                    linkTo(
                        methodOn(SugestaoController.class).ler(sugestao.getId())
                    )
                    .withSelfRel()
                );
                
                // Adiciona ?? resposta um link para a listagem de mercados.
                sugestao.add(
                    linkTo(
                        methodOn(MercadoController.class).listar(new Mercado())
                    )
                    .withRel("collection")
                );

                // Adiciona ?? resposta um link para a leitura do produto em quest??o.
                sugestao.add(
                    linkTo(
                        methodOn(ProdutoController.class).ler(produtoId)
                    )
                    .withRel("produto")
                );

                // Adiciona ?? resposta um link para a leitura da sugest??o em quest??o
                sugestao.add(
                    linkTo(
                        methodOn(MercadoController.class).ler(id)
                    )
                    .withRel("mercado")
                );

                // Adiciona ?? resposta um link para a leitura do estoque em quest??o.
                sugestao.add(
                    linkTo(
                        methodOn(EstoqueController.class).ler(estoque.getId())
                    )
                    .withRel("estoque")
                );
            }

            return responseSugestao;

        } catch (NoSuchElementException e) {
            // Lan??a uma exce????o informando que algum registro n??o foi encontrado.
            throw new ResponseStatusException(404, e.getMessage(), e);
        } catch (Exception e) {
            // Lan??a uma exce????o informando que ocorreu um erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * M??todo respons??vel por listar todos os mercados.
     * @param mercado - Objeto do tipo Mercado que ser?? usado como par??metro para filtrar os resultados.
     * @return <b>List < ResponseMercado ></b> - Lista de mercados.
     */
    @GetMapping
    public List<ResponseMercado> listar(Mercado requestMercado) {
        try {

            // Validando o mercado enviado como par??metro pelo cliente.
            Tratamento.validarMercado(requestMercado, true);

            // Buscando todos os mercados de acordo com o filtro enviado por par??metro e salvando na lista de mercados
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

            // Se a lista de marcados a serem retornados n??o for vazia
            if(!responseMercado.isEmpty()) {
                // Para cada marcado da resposta
                for(ResponseMercado mercado : responseMercado) {
                    // Adiciona ?? resposta um link para a leitura do marcado em quest??o.
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
            // Lan??a uma exce????o informando que os dados enviados pelo cliente s??o inv??lidos.
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (NullPointerException e) {
            // Lan??a uma exce????o caso algum registro seja nulo
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (UnsupportedOperationException e) {
            // Lan??a uma exce????o caso ocorra algum erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (Exception e) {
            // Lan??a uma exce????o caso ocorra algum erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * M??todo respons??vel por listar todos os mercados com pagina????o.
     * @param requestMercado - Objeto do mercado que ser?? usado como par??metro para buscar o registro.
     * @param pagina - N??mero da p??gina que ser?? exibida, contada a partir do 0.
     * @param limite - N??mero de registros que ser??o exibidos por p??gina.
     * @return <b>ResponseMercado</b> - Mercado encontrado.
     */
    @GetMapping(params = { "pagina", "limite" })
    public ResponsePagina listar(
        Mercado requestMercado,
        @RequestParam(required = false, defaultValue = "0") Integer pagina,
        @RequestParam(required = false, defaultValue = "10") Integer limite
    ) {
        try {
            // Validando o mercado enviado como par??metro pelo cliente.
            Tratamento.validarMercado(requestMercado, true);

            // Buscando todos os mercados de acordo com o filtro enviado por par??metro e salvando na p??gina de Mercados.
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

            // Criando um objeto do tipo ResponsePagina, que ser?? retornado com os mercados e os dados de pagina????o.
            ResponsePagina responseMercado = PaginaUtils.criarResposta(pagina, limite, paginaMercado, mercados);

            // Adiciona ?? resposta um link para a primeira p??gina da listagem de mercados.
            responseMercado.add(
                linkTo(
                    methodOn(MercadoController.class).listar(requestMercado, 0, limite)
                )
                .withRel("first")
            );

            // Se a p??gina de mercados n??o estiver vazia.
            if(!paginaMercado.isEmpty()) {
                // Se a p??gina informada pelo cliente n??o for a primeira p??gina da listagem de mercados.
                if(pagina > 0) {
                    // Adiciona ?? resposta um link para a p??gina anterior da listagem de mercados.
                    responseMercado.add(
                        linkTo(
                            methodOn(MercadoController.class).listar(requestMercado, pagina - 1, limite)
                        )
                        .withRel("previous")
                    );
                }

                // Se a p??gina informada pelo cliente n??o for a ??ltima p??gina da listagem de mercados.
                if(pagina < paginaMercado.getTotalPages() - 1) {
                    // Adiciona ?? resposta um link para a p??gina seguinte da listagem de mercados.
                    responseMercado.add(
                        linkTo(
                            methodOn(MercadoController.class).listar(requestMercado, pagina + 1, limite)
                        )
                        .withRel("next")
                    );
                }

                // Adiciona ?? resposta um link para a ??ltima p??gina da listagem de mercados.
                responseMercado.add(
                    linkTo(
                        methodOn(MercadoController.class).listar(requestMercado, paginaMercado.getTotalPages() - 1, limite)
                    )
                    .withRel("last")
                );

                // Para cada marcado da resposta
                for(ResponseMercado mercado : mercados) {
                    // Adiciona ?? resposta um link para a leitura do marcado em quest??o.
                    mercado.add(
                        linkTo(
                            methodOn(MercadoController.class).ler(mercado.getId())
                        )
                        .withSelfRel()
                    );
                }
            }

            // Retorna a p??gina com os marcados encontrados e as informa????es da pagina????o.
            return responseMercado;

        } catch (DadosInvalidosException e) {
            // Lan??a uma exce????o informando que os dados enviados pelo cliente s??o inv??lidos.
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            // Lan??a uma exce????o caso ocorra algum erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (NullPointerException e) {
            // Lan??a uma exce????o caso algum registro seja nulo
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (Exception e) {
            // Lan??a uma exce????o caso ocorra algum erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * M??todo respons??vel por atualizar alguns atributos espec??ficos de um mercado.
     * @param id - Id do mercado que ser?? atualizado.
     * @param requestMercado - Par??metros que ser??o atualizados no mercado.
     * @return <b>ResponseMercado</b> - Mercado atualizado.
     */
    @PatchMapping("/{id}")
    public ResponseMercado editar(@PathVariable int id, @RequestBody Mercado requestMercado) {
        try {

            // Validando os par??metros enviados pelo cliente.
            Tratamento.validarMercado(requestMercado, true);

            // Buscando um mercado que possua o mesmo endere??o informado
            if(mercadoRepository.findByEndereco(
                requestMercado.getLogradouro(), 
                requestMercado.getNumero(),
                requestMercado.getComplemento(), 
                requestMercado.getBairro(), 
                requestMercado.getCidade(),
                requestMercado.getUf(), 
                requestMercado.getCep()) != null
            )
                // Lan??a uma exce????o informando que o endere??o j?? existe.
                throw new DadosConflitantesException("mercado_existente");
            
            // Buscando o mercado que ser?? atualizado e guardando seu estado atual.
            Mercado mercadoAtual = mercadoRepository.findById(id).get();

            // Se o ramo informado n??o existir
            if(!ramoRepository.existsById(requestMercado.getRamoId()))
                // Lan??a uma exce????o informando que o ramo informado n??o existe.
                throw new DadosInvalidosException("ramo_nao_encontrado");

            // Se o nome do mercado j?? existir
            if(mercadoRepository.existsByNomeIgnoreCase(requestMercado.getNome()))
                // Lan??a uma exce????o informando que o nome do mercado j?? existe.
                throw new DadosConflitantesException("mercado_existente");

            // Atualizando o mercado com os dados enviados pelo cliente.
            ResponseMercado responseMercado = new ResponseMercado(
                mercadoRepository.save(
                    // Chamando m??todo que retornar?? o mercado com os dados atualizados de acordo com os par??metros recebidos.
                    EditaRecurso.editarMercado(
                        mercadoAtual, 
                        requestMercado
                    )
                )
            );

            // Adiciona ?? resposta um link para a leitura do mercado em quest??o.
            responseMercado.add(
                linkTo(
                    methodOn(MercadoController.class).ler(responseMercado.getId())
                )
                .withSelfRel()
            );

            // Retorna o mercado atualizado.
            return responseMercado;

        } catch (DadosConflitantesException e) {
            // Lan??a uma exce????o informando que os dados enviados pelo cliente s??o conflitantes/j?? existem no banco de dados.
            throw new ResponseStatusException(409, e.getMessage(), e);
        } catch (DadosInvalidosException e) {
            // Lan??a uma exce????o informando que os dados enviados pelo cliente s??o inv??lidos.
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (DataIntegrityViolationException e) {
            // Lan??a uma exce????o informando que ocorreu um erro de integridade de dados.
            throw new ResponseStatusException(500, "erro_insercao", e);
        } catch (IllegalArgumentException e) {
            // Lan??a uma exce????o caso ocorra algum erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (NoSuchElementException e) {
            // Lan??a uma exce????o caso algum registro n??o seja encontrado.
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (Exception e) {
            // Lan??a uma exce????o caso ocorra algum erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * M??todo respons??vel por atualizar todos os atributos de um mercado.
     * @param id - Id do mercado que ser?? atualizado.
     * @param requestMercado - Par??metros que ser??o atualizados no mercado.
     * @return <b>ResponseMercado</b> - Mercado atualizado.
     */
    @PutMapping("/{id}")
    public ResponseMercado atualizar(@PathVariable int id, @RequestBody Mercado requestMercado) {
        try {

            // Validando os par??metros enviados pelo cliente.
            Tratamento.validarMercado(requestMercado, false);

            // Se o id do mercado informado n??o existir
            if(!mercadoRepository.existsById(id))
                // Lan??a uma exce????o informando que o mercado n??o existe.
                throw new NoSuchElementException("nao_encontrado");

            // Se o id do usu??rio informado n??o existir
            if(!usuarioRepository.existsById(requestMercado.getCriadoPor()))
                // Lan??a uma exce????o informando que o usu??rio n??o existe.
                throw new DadosInvalidosException("usuario_nao_encontrado");

            // Se o id do ramo informado n??o existir
            if(!ramoRepository.existsById(requestMercado.getRamoId()))
                // Lan??a uma exce????o informando que o ramo informado n??o existe.
                throw new DadosInvalidosException("ramo_nao_encontrado");

            // Se o nome do mercado j?? existir no banco de dados
            if(mercadoRepository.existsByNomeIgnoreCase(requestMercado.getNome()))
                // Lan??a uma exce????o informando que o nome do mercado j?? existe.
                throw new DadosConflitantesException("mercado_existente");

            // Se o endere??o do mercado j?? existir no banco de dados
            if(mercadoRepository.findByEndereco(
                requestMercado.getLogradouro(), 
                requestMercado.getNumero(),
                requestMercado.getComplemento(), 
                requestMercado.getBairro(), 
                requestMercado.getCidade(),
                requestMercado.getUf(), 
                requestMercado.getCep()) != null
            )
                // Lan??a uma exce????o informando que o endere??o j?? existe.
                throw new DadosConflitantesException("mercado_existente");

            Mercado mercado = mercadoRepository.findById(id).get();

            // Adiciona ao objeto da requisi????o o Id informado pelo cliente.
            requestMercado.setId(id);

            // Adiciona ao objeto da requisi????o o Id do usu??rio que criou o mercado.
            requestMercado.setCriadoPor(mercado.getCriadoPor());

            // Atualizando o mercado com os dados enviados pelo cliente e armazenando no objeto responseMercado.
            ResponseMercado responseMercado = new ResponseMercado(mercadoRepository.save(requestMercado));

            // Adiciona ?? resposta um link para a leitura do mercado em quest??o.
            responseMercado.add(
                linkTo(
                    methodOn(MercadoController.class).ler(responseMercado.getId())
                )
                .withSelfRel()
            );

            // Retorna o mercado atualizado.
            return responseMercado;

        } catch (DadosConflitantesException e) {
            // Lan??a uma exce????o informando que os dados enviados pelo cliente s??o conflitantes/j?? existem no banco de dados.
            throw new ResponseStatusException(409, e.getMessage(), e);
        } catch (DadosInvalidosException e) {
            // Lan??a uma exce????o informando que os dados enviados pelo cliente s??o inv??lidos.
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (NoSuchElementException e) {
            // Lan??a uma exce????o informando que algum registro n??o foi encontrado.
            throw new ResponseStatusException(404, e.getMessage(), e);
        } catch (DataIntegrityViolationException e) {
            // Lan??a uma exce????o informando que ocorreu um erro de integridade de dados.
            throw new ResponseStatusException(500, "erro_insercao", e);
        } catch (IllegalArgumentException e) {
            // Lan??a uma exce????o caso ocorra algum erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (Exception e) {
            // Lan??a uma exce????o caso ocorra algum erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * M??todo respons??vel por deletar um mercado.
     * @param id - Id do mercado que ser?? deletado.
     * @return Object - Link para a listagem de mercados.
     */
    @DeleteMapping("/{id}")
    public Object remover(@PathVariable int id) {
        try {

            // Se o id do mercado informado n??o existir
            if(!mercadoRepository.existsById(id))
                // Lan??a uma exce????o informando que o mercado n??o existe.
                throw new NoSuchElementException("nao_encontrado");

            // Deleta o mercado com o id informado.
            mercadoRepository.deleteById(id);

            // Retorna o link para a listagem de mercados.
            return linkTo(
                        methodOn(MercadoController.class).listar(new Mercado())
                    )
                    .withRel("collection");

        } catch (NoSuchElementException e) {
            // Lan??a uma exce????o informando que o mercado n??o existe.
            throw new ResponseStatusException(404, e.getMessage(), e);
        } catch (DataIntegrityViolationException e) {
            // Lan??a uma exce????o informando que ocorreu um erro de integridade de dados.
            throw new ResponseStatusException(500, "erro_remocao", e);
        } catch (IllegalArgumentException e) {
            // Lan??a uma exce????o caso ocorra algum erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (Exception e) {
            // Lan??a uma exce????o caso ocorra algum erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }
}
