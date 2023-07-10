package com.pagueibaratoapi.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.pagueibaratoapi.models.exceptions.DadosConflitantesException;
import com.pagueibaratoapi.models.exceptions.DadosInvalidosException;
import com.pagueibaratoapi.models.requests.Categoria;
import com.pagueibaratoapi.models.responses.ResponseCategoria;
import com.pagueibaratoapi.repository.CategoriaRepository;
import com.pagueibaratoapi.utils.EditaRecurso;
import com.pagueibaratoapi.utils.Tratamento;

/**
 * Classe responsável por controlar as requisições da categoria.
 */
@RestController
@RequestMapping("/categoria")
public class CategoriaController {

    // Repositório da categoria, responsável pelos métodos JPA no banco.
    private final CategoriaRepository categoriaRepository;

    // Construtor
    public CategoriaController(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    /**
     * Rota responsável por criar uma categoria.
     * @param categoria - Dados da categoria a ser criada.
     * @return Dados e id da categoria criada.
     */
    @PostMapping
    @CacheEvict(value = "Categorias", allEntries = true)
    public ResponseCategoria criar(@RequestBody Categoria requestCategoria) {
        try {

            // Valida os dados fornecidos.
            Tratamento.validarCategoria(requestCategoria, false);

            // Se existir alguma categoria com o mesmo nome no banco,
            if(categoriaRepository.existsByNomeIgnoreCase(requestCategoria.getNome()))
                // Retorna erro.
                throw new DadosConflitantesException("nome_existente");

            // Insere a categoria e transforma os dados obtidos em modelo de resposta.
            ResponseCategoria responseCategoria = new ResponseCategoria(categoriaRepository.save(requestCategoria));

            // Adiciona o link para a categoria.
            responseCategoria.add(
                linkTo(
                    methodOn(CategoriaController.class).ler(responseCategoria.getId())
                )
                .withSelfRel()
            );

            // Retorna os dados da categoria.
            return responseCategoria;

        } catch (DadosConflitantesException e) {
            throw new ResponseStatusException(409, e.getMessage(), e);
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
     * Rota responsável por ler uma categoria específica.
     * @param id - Id da categoria a ser lida.
     * @return Dados da categoria.
     */
    @GetMapping("/{id}")
    public ResponseCategoria ler(@PathVariable("id") Integer id) {
        try {

            // Busca a categoria no banco e transforma os dados obtidos em modelo de resposta.
            ResponseCategoria responseCategoria = new ResponseCategoria(categoriaRepository.findById(id).get());

            // Adiciona o link para a rota de listagem das categorias.
            if(responseCategoria != null) {
                responseCategoria.add(
                    linkTo(
                        methodOn(CategoriaController.class).listar()
                    )
                    .withRel("collection")
                );
            }

            // Retorna os dados da categoria.
            return responseCategoria;

        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Rota responsável por listar todas as categorias.
     * @return Lista de categorias.
     */
    @GetMapping
    @Cacheable("Categorias")
    public List<ResponseCategoria> listar() {
        try {
            
            // Busca todas as categorias e cria uma array de resposta.
            List<Categoria> categorias = categoriaRepository.findAll();
            List<ResponseCategoria> responseCategoria = new ArrayList<ResponseCategoria>();

            // Para cada categoria, transforma os dados obtidos em modelo de resposta.
            for(Categoria categoria : categorias) {
                responseCategoria.add(
                    new ResponseCategoria(categoria)
                );
            }

            // Se a lista de resposta não estiver vazia,
            if(!responseCategoria.isEmpty()) {
                // Itera sobres as categorias
                for(ResponseCategoria categoria : responseCategoria) {
                    // E adiciona o link para a categoria atual
                    categoria.add(
                        linkTo(
                            methodOn(CategoriaController.class).ler(categoria.getId())
                        )
                        .withSelfRel()
                    );
                }
            }

            // Retorna a lista de categorias.
            return responseCategoria;

        } catch (NullPointerException e) {
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (UnsupportedOperationException e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (Exception e) {
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Rota responsável por editar uma categoria.
     * @param id - Id da categoria a ser editada.
     * @param categoria - Dados modificados da categoria.
     * @return Dados novos da categoria editada.
     */
    @PatchMapping("/{id}")
    public ResponseCategoria editar(@PathVariable int id, @RequestBody Categoria requestCategoria) {
        try {

            // Valida os dados fornecidos.
            Tratamento.validarCategoria(requestCategoria, true);

            // Se existir alguma categoria com o mesmo nome no banco,
            if(categoriaRepository.existsByNomeIgnoreCase(requestCategoria.getNome()))
                // Retorna erro.
                throw new DadosConflitantesException("nome_existente");

            // Busca a categoria para ser editada.
            Categoria categoriaAtual = categoriaRepository.findById(id).get();

            // Chama o recurso de tratamento de edição de categoria.
            // Insere os dados tratados no banco de dados.
            // Transforma os dados inseridos em resposta.
            ResponseCategoria responseCategoria = new ResponseCategoria(
                categoriaRepository.save(
                    EditaRecurso.editarCategoria(
                        categoriaAtual,
                        requestCategoria
                    )
                )
            );

            // Adiciona o link para a categoria editada.
            responseCategoria.add(
                linkTo(
                    methodOn(CategoriaController.class).ler(responseCategoria.getId())
                )
                .withSelfRel()
            );

            // Retorna os dados da categoria editada.
            return responseCategoria;

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
     * Rota responsável por substituir uma categoria.
     * @param id - Id da categoria a ser substituída.
     * @param requestCategoria - Dados da nova categoria.
     * @return Dados da nova categoria inserida.
     */
    @PutMapping("/{id}")
    public ResponseCategoria atualizar(@PathVariable int id, @RequestBody Categoria requestCategoria) {
        try {

            // Se existir alguma categoria com o mesmo nome no banco,
            if(categoriaRepository.existsByNomeIgnoreCase(requestCategoria.getNome()))
                // Retorna erro.
                throw new DadosConflitantesException("nome_existente");

            // Valida os dados fornecidos.
            Tratamento.validarCategoria(requestCategoria, false);

            // Se não existir alguma categoria com o id fornecido,
            if(!categoriaRepository.existsById(id))
                // Retorna erro.
                throw new NoSuchElementException("nao_encontrado");

            // Define o id da categoria como o id fornecido.
            requestCategoria.setId(id);

            // Insere a categoria e transforma os dados inseridos em resposta.
            ResponseCategoria responseCategoria = new ResponseCategoria(categoriaRepository.save(requestCategoria));

            // Adiciona o link para a categoria inserida.
            responseCategoria.add(
                linkTo(
                    methodOn(CategoriaController.class).ler(responseCategoria.getId())
                )
                .withSelfRel()
            );

            // Retorna os dados da categoria nova.
            return responseCategoria;

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
     * Rota responsável por excluir uma categoria.
     * @param id - Id da categoria a ser excluída.
     */
    @DeleteMapping("/{id}")
    @CacheEvict(value = "Categorias", allEntries = true)
    public Object remover(@PathVariable int id) {
        try {

            // Se não existir alguma categoria com o id fornecido,
            if(!categoriaRepository.existsById(id))
                // Retorna erro.
                throw new NoSuchElementException("nao_encontrado");

            // Exclui a categoria com o id fornecido.
            categoriaRepository.deleteById(id);

            // Retorna o link para a rota responsável por listar todas as categorias.
            return linkTo(
                        methodOn(CategoriaController.class).listar()
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
