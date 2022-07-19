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

import com.pagueibaratoapi.models.responses.ResponsePagina;
import com.pagueibaratoapi.models.responses.ResponseSugestao;
import com.pagueibaratoapi.models.exceptions.DadosInvalidosException;
import com.pagueibaratoapi.models.requests.Sugestao;
import com.pagueibaratoapi.models.requests.Usuario;
import com.pagueibaratoapi.repository.EstoqueRepository;
import com.pagueibaratoapi.repository.SugestaoRepository;
import com.pagueibaratoapi.repository.UsuarioRepository;
import com.pagueibaratoapi.utils.EditaRecurso;
import com.pagueibaratoapi.utils.PaginaUtils;
import com.pagueibaratoapi.utils.Tratamento;

/**
 * Classe responsável por controlar as requisições de sugestões.
 */
@RestController
@RequestMapping("/sugestao")
public class SugestaoController {

    // Repositórios responsáveis pelos métodos JPA do banco de dados.
    private final EstoqueRepository estoqueRepository;
    private final SugestaoRepository sugestaoRepository;
    private final UsuarioRepository usuarioRepository;

    // Construtor.
    public SugestaoController(
        EstoqueRepository estoqueRepository,
        SugestaoRepository sugestaoRepository,
        UsuarioRepository usuarioRepository
    ) {
        this.estoqueRepository = estoqueRepository;
        this.sugestaoRepository = sugestaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Método responsável por criar uma nova sugestão.
     * @param sugestao - Dados da sugestão a ser criada.
     * @return Dados e id da sugestão criada.
     */
    @PostMapping
    public ResponseSugestao criar(@RequestBody Sugestao requestSugestao) {
        try {

            // Valida os dados fornecidos.
            Tratamento.validarSugestao(requestSugestao, false);

            // Se o estoque informado não existir,
            if(!estoqueRepository.existsById(requestSugestao.getEstoqueId()))
                // Retorna erro.
                throw new DadosInvalidosException("estoque_nao_encontrado");

            // Se o usuário informado não existir,
            if(!usuarioRepository.existsById(requestSugestao.getCriadoPor()))
                // Retorna erro.
                throw new DadosInvalidosException("usuario_nao_encontrado");

            Usuario usuario = usuarioRepository.findById(requestSugestao.getCriadoPor()).get();

            // Validando se o usuário informado como criador já foi excluído.
            if(!Tratamento.usuarioExiste(usuario))
                // Retorna erro.
                throw new NoSuchElementException("usuario_nao_encontrado");

            // Elimina os decimais do preço multiplicando por 100.
            requestSugestao.setPreco(requestSugestao.getPreco() * 100);
            
            // Insere a sugestão e transforma os dados obtidos em modelo de resposta.
            ResponseSugestao responseSugestao = new ResponseSugestao(sugestaoRepository.save(requestSugestao));

            // Adiciona o link para a sugestão.
            responseSugestao.add(
                linkTo(
                    methodOn(SugestaoController.class).ler(responseSugestao.getId())
                )
                .withSelfRel()
            );

            // Divide o preço por 100 para obter os centavos.
            responseSugestao.setPreco(responseSugestao.getPreco() / 100);

            // Retorna a sugestão criada.
            return responseSugestao;

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
     * Método responsável por ler uma sugestão.
     * @param id - Id da sugestão a ser lida.
     * @return Dados da sugestão lida.
     */
    @GetMapping("/{id}")
    public ResponseSugestao ler(@PathVariable(value = "id") Integer id) {
        try {

            // Busca a sugestão no banco e transforma os dados obtidos em modelo de resposta.
            ResponseSugestao responseSugestao = new ResponseSugestao(sugestaoRepository.findById(id).get());

            // Adiciona o link para a rota de listagem de sugestões.
            if(responseSugestao != null) {
                // Divide o preço por 100 para obter os centavos.
                responseSugestao.setPreco(responseSugestao.getPreco() / 100);
                responseSugestao.add(
                    linkTo(
                        methodOn(SugestaoController.class).listar(new Sugestao())
                    )
                    .withRel("collection")
                );
            }

            // Retorna a sugestão.
            return responseSugestao;

        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Método responsável por listar as sugestões.
     * @param requestSugestao - Dados de pesquisa para filtragem.
     * @return Lista de sugestões.
     */
    @GetMapping
    public List<ResponseSugestao> listar(Sugestao requestSugestao) {
        try {

            // Valida os dados fornecidos.
            Tratamento.validarSugestao(requestSugestao, true);

            // Busca as sugestões no banco semelhantes aos dados de pesquisa.
            // Se não houver dados de pesquisa, busca todas as sugestões.
            List<Sugestao> sugestoes = sugestaoRepository.findAll(
                Example.of(
                    requestSugestao, 
                    ExampleMatcher
                        .matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                )
            );

            // Lista de sugestões que será retornada.
            List<ResponseSugestao> responseSugestao = new ArrayList<ResponseSugestao>();

            // Adiciona as sugestões a lista de sugestões.
            for(Sugestao sugestao : sugestoes) {
                responseSugestao.add(new ResponseSugestao(sugestao));
            }

            // Se houver sugestões,
            if(!responseSugestao.isEmpty()) {
                // Percorre as sugestões,
                for(ResponseSugestao sugestao : responseSugestao) {
                    // Divide o preço da sugestão atual por 100 para obter os centavos.
                    sugestao.setPreco(sugestao.getPreco() / 100);
                    // Adiciona o link para detalhamento da sugestão.
                    sugestao.add(
                        linkTo(
                            methodOn(SugestaoController.class).ler(sugestao.getId())
                        )
                        .withSelfRel()
                    );
                }
            }

            // Retorna as sugestões.
            return responseSugestao;

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
     * Rota responsável por listar sugestões.
     * @param requestSugestao - Dados de pesquisa para filtragem.
     * @param pagina - Numero da página a ser mostrada.
     * @param limite - Limite de registros por página.
     * @return Lista de sugestões com dados da página.
     */
    @GetMapping(params = { "pagina", "limite" })
    public ResponsePagina listar(
        Sugestao requestSugestao,
        @RequestParam(required = false, defaultValue = "0") Integer pagina,
        @RequestParam(required = false, defaultValue = "10") Integer limite
    ) {
        try {

            // Valida os dados fornecidos.
            Tratamento.validarSugestao(requestSugestao, true);

            // Busca as sugestões no banco semelhantes aos dados de pesquisa.
            // Se não houver dados de pesquisa, busca todas as sugestões.
            // Informa os dados de paginação.
            // Se não houver paginação, busca todas as sugestões.
            Page<Sugestao> paginaSugestao = sugestaoRepository.findAll(
                Example.of(
                    requestSugestao, 
                    ExampleMatcher
                        .matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                ),
                PageRequest.of(pagina, limite)
            );

            // Lista de sugestões que será retornada.
            List<ResponseSugestao> sugestoes = new ArrayList<ResponseSugestao>();

            // Formata a resposta com os dados obtidos.
            ResponsePagina responsePagina = PaginaUtils.criarResposta(pagina, limite, paginaSugestao, sugestoes);

            // Adiciona as sugestões a lista.
            for(Sugestao sugestao : paginaSugestao.getContent()) {
                sugestoes.add(new ResponseSugestao(sugestao));
            }

            // Adiciona o link para a primeira página de sugestões.
            responsePagina.add(
                linkTo(
                    methodOn(SugestaoController.class).listar(requestSugestao, 0, limite)
                )
                .withRel("first")
            );

            // Se houver sugestões,
            if(!paginaSugestao.isEmpty()) {
                // Se a página atual não for a primeira,
                if(pagina > 0) {
                    // Adiciona o link para a página anterior.
                    responsePagina.add(
                        linkTo(
                            methodOn(SugestaoController.class).listar(requestSugestao, pagina - 1, limite)
                        )
                        .withRel("previous")
                    );
                }

                // Se a página atual não for a última,
                if(pagina < paginaSugestao.getTotalPages() - 1) {
                    // Adiciona o link para a página seguinte.
                    responsePagina.add(
                        linkTo(
                            methodOn(SugestaoController.class).listar(requestSugestao, pagina + 1, limite)
                        )
                        .withRel("next")
                    );
                }

                // Adiciona o link para a última página de sugestões.
                responsePagina.add(
                    linkTo(
                        methodOn(SugestaoController.class).listar(requestSugestao, paginaSugestao.getTotalPages() - 1, limite)
                    )
                    .withRel("last")
                );

                // Percorre as sugestões,
                for(ResponseSugestao sugestao : sugestoes) {
                    // Divide o preço da sugestão atual por 100 para obter os centavos.
                    sugestao.setPreco(sugestao.getPreco() / 100);
                    // Adiciona o link para detalhamento da sugestão.
                    sugestao.add(
                        linkTo(
                            methodOn(SugestaoController.class).ler(sugestao.getId())
                        )
                        .withSelfRel()
                    );
                }
            }

            // Retorna as sugestões.
            return responsePagina;

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
     * Rota responsável por editar uma sugestão.
     * @param id - Id da sugestão a ser editada.
     * @param requestSugestao - Dados modificados da sugestão.
     * @return Dados novos da sugestão editada.
     */
    @PatchMapping("/{id}")
    public ResponseSugestao editar(@PathVariable int id, @RequestBody Sugestao requestSugestao) {
        try {

            // Valida os dados fornecidos.
            Tratamento.validarSugestao(requestSugestao, true);

            // Obtém a sugestão a ser editada.
            Sugestao sugestaoAtual = sugestaoRepository.findById(id).get();
    
            // Chama o recurso de tratamento para editar a sugestão.
            // Insere os dados modificados no banco.
            // Transforma os dados obtidos em modelo de resposta.
            ResponseSugestao responseSugestao = new ResponseSugestao(
                sugestaoRepository.save(
                    EditaRecurso.editarSugestao(
                        sugestaoAtual, 
                        requestSugestao
                    )
                )
            );

            // Adiciona o link para detalhamento da sugestão.
            responseSugestao.add(
                linkTo(
                    methodOn(SugestaoController.class).ler(responseSugestao.getId())
                )
                .withSelfRel()
            );

            // Divide o preço por 100 para obter os centavos.
            responseSugestao.setPreco(responseSugestao.getPreco() / 100);

            // Retorna a sugestão editada.
            return responseSugestao;

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
     * Rota responsável por substituir uma sugestão.
     * @param id - Id da sugestão a ser substituída.
     * @param requestSugestao - Dados da nova sugestão.
     * @return Dados da nova sugestão.
     */
    @PutMapping("/{id}")
    public ResponseSugestao atualizar(@PathVariable int id, @RequestBody Sugestao requestSugestao) {
        try {

            // Valida os dados fornecidos.
            Tratamento.validarSugestao(requestSugestao, true);

            // Se o estoque fornecido não existir.
            if(!estoqueRepository.existsById(requestSugestao.getEstoqueId()))
                // Retorna erro.
                throw new DadosInvalidosException("estoque_nao_encontrado");

            // Se o usuário fornecido não existir.
            if(!usuarioRepository.existsById(requestSugestao.getCriadoPor()))
            // Retorna erro.
                throw new DadosInvalidosException("usuario_nao_encontrado");

            // Usuário que criou a sugestão.
            Sugestao sugestao = sugestaoRepository.findById(id).get();

            // Define o id da sugestão a ser substituída.
            requestSugestao.setId(id);

            // Definindo o usuário que criou a sugestão como o usuário que está no banco, haja vista que não pode ser alterado.
            requestSugestao.setCriadoPor(sugestao.getCriadoPor());

            // Multiplica o preço por 100 para eliminar o decimal.
            requestSugestao.setPreco(requestSugestao.getPreco() * 100);

            // Insere a nova sugestão no banco e transforma os dados obtidos em modelo de resposta.
            ResponseSugestao responseSugestao = new ResponseSugestao(sugestaoRepository.save(requestSugestao));

            // Adiciona o link para detalhamento da sugestão.
            responseSugestao.add(
                linkTo(
                    methodOn(SugestaoController.class).ler(responseSugestao.getId())
                )
                .withSelfRel()
            );

            // Divide o preço por 100 para obter os centavos.
            responseSugestao.setPreco(responseSugestao.getPreco() / 100);

            // Retorna a nova sugestão.
            return responseSugestao;

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
     * Rota responsável por excluir uma sugestão.
     * @param id - Id da sugestão a ser excluída.
     */
    @DeleteMapping("/{id}")
    public Object remover(@PathVariable int id) {
        try {

            // Se a sugestão fornecida não existir.
            if(!sugestaoRepository.existsById(id))
            // Retorna erro.
                throw new NoSuchElementException("nao_encontrado");

            // Exclui a sugestão do banco.
            sugestaoRepository.deleteById(id);

            // Retorna o link para listagem de sugestões.
            return linkTo(
                        methodOn(SugestaoController.class).listar(new Sugestao())
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
