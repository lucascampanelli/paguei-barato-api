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
import com.pagueibaratoapi.models.requests.Ramo;
import com.pagueibaratoapi.models.responses.ResponseRamo;
import com.pagueibaratoapi.repository.RamoRepository;
import com.pagueibaratoapi.utils.EditaRecurso;
import com.pagueibaratoapi.utils.Tratamento;

/**
 * Classe responsável por controlar as requisições dos Ramos.
 */
@RestController
@RequestMapping("/ramo")
public class RamoController {

    // Iniciando as variáveis de instância do repositório.
    private final RamoRepository ramoRepository;

    // Construtor do controller do ramo, que realiza a injeção de dependência do repositório.
    public RamoController(RamoRepository ramoRepository) {
        this.ramoRepository = ramoRepository;
    }

    /**
     * Método responsável por criar um novo ramo.
     * @param requestRamo - Objeto do tipo Ramo que contém os dados do novo ramo.
     * @return ResponseRamo - Objeto do tipo ResponseRamo com o novo Ramo criado.
     */
    @PostMapping
    @CacheEvict(value = "ramos", allEntries = true)
    public ResponseRamo criar(@RequestBody Ramo requestRamo) {
        try {

            // Validando os dados enviados como parâmetro pelo cliente.
            Tratamento.validarRamo(requestRamo, false);

            // Verifica se o nome do ramo já existe.
            if(ramoRepository.existsByNomeIgnoreCase(requestRamo.getNome()))
                // Lança exceção caso o nome do ramo já exista.
                throw new DadosConflitantesException("ramo_existente");

            // Criando o novo ramo e salvando-o na variável de resposta do tipo ResponseRamo,
            ResponseRamo responseRamo = new ResponseRamo(ramoRepository.save(requestRamo));

            // Adicionando à resposta o link para leitura do ramo criado.
            responseRamo.add(
                linkTo(
                    methodOn(RamoController.class).ler(responseRamo.getId())
                )
                .withSelfRel()
            );

            // Retornando a resposta.
            return responseRamo;

        } catch (DadosConflitantesException e) {
            // Lança uma exceção indicando que o nome do ramo já existe.
            throw new ResponseStatusException(409, e.getMessage(), e);
        } catch (DadosInvalidosException e) {
            // Lança uma exceção indicando que os dados enviados são inválidos.
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (DataIntegrityViolationException e) {
            // Lança uma exceção indicando que ocorreu um erro na integridade dos dados.
            throw new ResponseStatusException(500, "erro_insercao", e);
        } catch (IllegalArgumentException e) {
            // Lança uma exceção indicando que os dados enviados são inválidos.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (Exception e) {
            // Lança uma exceção indicando que ocorreu um erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Método responsável por ler um ramo com o id informado.
     * @param id - Id do ramo que será lido.
     * @return ResponseRamo - Objeto do tipo ResponseRamo com o ramo lido.
     */
    @GetMapping("/{id}")
    public ResponseRamo ler(@PathVariable("id") Integer id) {
        try {

            // Buscando o ramo com o id informado e salvando num objeto de resposta do Ramo.
            ResponseRamo responseRamo = new ResponseRamo(ramoRepository.findById(id).get());

            // Adicionando à resposta o link para listagem dos ramos.
            responseRamo.add(
                linkTo(
                    methodOn(RamoController.class).listar(new Ramo())
                )
                .withRel("collection")
            );

            // Retornando a resposta com o ramo encontrado.
            return responseRamo;

        } catch (NoSuchElementException e) {
            // Lança uma exceção indicando que o ramo não foi encontrado.
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (Exception e) {
            // Lança uma exceção indicando que ocorreu um erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Método responsável por listar todos os ramos.
     * @param requestRamo - Objeto do tipo Ramo que contém os dados do ramo que será usado para filtrar a listagem.
     * @return <b>List< ResponseRamo ></b> - Lista de objetos do tipo ResponseRamo com os ramos encontrados.
     */
    @GetMapping
    @Cacheable("ramos")
    public List<ResponseRamo> listar(Ramo requestRamo) {
        try {

            // Validando os dados enviados como parâmetro pelo cliente.
            Tratamento.validarRamo(requestRamo, true);

            // Buscando todos os ramos que satisfaçam os dados enviados pelo cliente e salvando na lista de ramos.
            List<Ramo> ramos = ramoRepository.findAll(
                Example.of(
                    requestRamo, 
                    ExampleMatcher
                        .matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                )
            );

            // Criando uma lista de objetos do tipo ResponseRamo vazia.
            List<ResponseRamo> responseRamo = new ArrayList<ResponseRamo>();

            // Para cada ramo da lista de Ramos
            for(Ramo ramo : ramos) {
                // Criando um novo objeto do tipo ResponseRamo com os dados do ramo atual e adicionando à lista de resposta.
                responseRamo.add(new ResponseRamo(ramo));
            }

            // Se a lista de resposta não estiver vazia.
            if(!responseRamo.isEmpty()) {
                // Para cada ramo da lista de resposta.
                for(ResponseRamo ramo : responseRamo) {
                    // Adicionando à resposta o link para leitura do ramo atual.
                    ramo.add(
                        linkTo(
                            methodOn(RamoController.class).ler(ramo.getId())
                        )
                        .withSelfRel()
                    );
                }
            }

            // Retornando a lista de resposta.
            return responseRamo;

        } catch (DadosInvalidosException e) {
            // Lança uma exceção indicando que os dados enviados pelo cliente são inválidos.
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (NullPointerException e) {
            // Lança uma exceção indicando que algum registro não foi encontrado.
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (UnsupportedOperationException e) {
            // Lança uma exceção indicando que ocorreu um erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (Exception e) {
            // Lança uma exceção indicando que ocorreu um erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Método responsável por atualizar alguns atributos específicos de um ramo.
     * @param id - Id do ramo que será atualizado.
     * @param requestRamo - Parâmetros que serão atualizados no ramo.
     * @return ResponseRamo - Objeto do tipo ResponseRamo com o ramo atualizado.
     */
    @PatchMapping("/{id}")
    public ResponseRamo editar(@PathVariable("id") Integer id, @RequestBody Ramo requestRamo) {
        try {

            // Validando os dados enviados como parâmetro pelo cliente.
            Tratamento.validarRamo(requestRamo, true);

            // Se o nome do ramo enviado pelo cliente já existir na base de dados.
            if(ramoRepository.existsByNomeIgnoreCase(requestRamo.getNome()))
                // Lança uma exceção indicando que o nome do ramo já existe.
                throw new DadosConflitantesException("ramo_existente");

            // Buscando o estado atual do ramo com o id informado.
            Ramo ramoAtual = ramoRepository.findById(id).get();
            
            // Atualizando o ramo e armazenando no objeto de resposta do ramo.
            ResponseRamo responseRamo = new ResponseRamo(
                ramoRepository.save(
                    // Atualizando os dados do ramo com os dados enviados pelo cliente.
                    EditaRecurso.editarRamo(
                        ramoAtual, 
                        requestRamo
                    )
                )
            );
            
            // Adicionando à resposta o link para leitura do ramo atualizado.
            responseRamo.add(
                linkTo(
                    methodOn(RamoController.class).ler(responseRamo.getId())
                )
                .withSelfRel()
            );

            // Retornando a resposta com o ramo atualizado.
            return responseRamo;

        } catch (DadosConflitantesException e) {
            // Lança uma exceção indicando que os dados enviados pelo cliente são conflitantes.
            throw new ResponseStatusException(409, e.getMessage(), e);
        } catch (DadosInvalidosException e) {
            // Lança uma exceção indicando que os dados enviados pelo cliente são inválidos.
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (DataIntegrityViolationException e) {
            // Lança uma exceção indicando que existe uma violação de integridade dos dados.
            throw new ResponseStatusException(500, "erro_insercao", e);
        } catch (IllegalArgumentException e) {
            // Lança uma exceção indicando que o parâmetro enviado pelo cliente é inválido.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (NoSuchElementException e) {
            // Lança uma exceção indicando que o registro não foi encontrado.
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (Exception e) {
            // Lança uma exceção indicando que ocorreu um erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Método responsável por atualizar todos os atributos de um ramo.
     * @param id - Id do ramo que será atualizado.
     * @param requestRamo - Parâmetros que serão atualizados no ramo.
     * @return ResponseRamo - Objeto do tipo ResponseRamo com o ramo atualizado.
     */
    @PutMapping("/{id}")
    public ResponseRamo atualizar(@PathVariable("id") Integer id, @RequestBody Ramo requestRamo) {
        try {

            // Validando os dados enviados como parâmetro pelo cliente.
            Tratamento.validarRamo(requestRamo, false);

            // Se o nome do ramo enviado pelo cliente já existir na base de dados.
            if(ramoRepository.existsByNomeIgnoreCase(requestRamo.getNome()))
                // Lança uma exceção indicando que o nome do ramo já existe.
                throw new DadosConflitantesException("ramo_existente");

            // Definindo o Id do Ramo enviado pelo cliente como o Id informado na URI do recurso.
            requestRamo.setId(id);

            // Atualizando o ramo e armazenando no objeto de resposta do ramo.
            ResponseRamo responseRamo = new ResponseRamo(ramoRepository.save(requestRamo));

            // Adicionando à resposta o link para leitura do ramo atualizado.
            responseRamo.add(
                linkTo(
                    methodOn(RamoController.class).ler(responseRamo.getId())
                )
                .withSelfRel()
            );

            // Retornando a resposta com o ramo atualizado.
            return responseRamo;

        } catch (DadosConflitantesException e) {
            // Lança uma exceção indicando que os dados enviados pelo cliente são conflitantes.
            throw new ResponseStatusException(409, e.getMessage(), e);
        } catch (DadosInvalidosException e) {
            // Lança uma exceção indicando que os dados enviados pelo cliente são inválidos.
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (NoSuchElementException e) {
            // Lança uma exceção indicando que o registro não foi encontrado.
            throw new ResponseStatusException(404, e.getMessage(), e);
        } catch (DataIntegrityViolationException e) {
            // Lança uma exceção indicando que existe uma violação de integridade dos dados.
            throw new ResponseStatusException(500, "erro_insercao", e);
        } catch (IllegalArgumentException e) {
            // Lança uma exceção indicando que o parâmetro enviado pelo cliente é inválido.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (Exception e) {
            // Lança uma exceção indicando que ocorreu um erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Método responsável por remover um ramo com o Id especificado.
     * @param id - Id do ramo que será removido.
     * @return Object - Link para a listagem dos ramos.
     */
    @DeleteMapping("/{id}")
    @CacheEvict(value = "ramos", allEntries = true)
    public Object remover(@PathVariable int id) {
        try {

            // Se não existir um ramo com o Id informado.
            if(!ramoRepository.existsById(id))
                // Lança uma exceção indicando que o registro não foi encontrado.
                throw new NoSuchElementException("nao_encontrado");
            
            // Removendo o ramo com o Id informado.
            ramoRepository.deleteById(id);

            // Retornando o link para a listagem dos ramos.
            return linkTo(
                        methodOn(RamoController.class).listar(new Ramo())
                    )
                    .withRel("collection");

        } catch (NoSuchElementException e) {
            // Lança uma exceção indicando que o registro não foi encontrado.
            throw new ResponseStatusException(404, e.getMessage(), e);
        } catch (DataIntegrityViolationException e) {
            // Lança uma exceção indicando que existe uma violação de integridade dos dados.
            throw new ResponseStatusException(500, "erro_remocao", e);
        } catch (IllegalArgumentException e) {
            // Lança uma exceção indicando que o parâmetro enviado pelo cliente é inválido.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (Exception e) {
            // Lança uma exceção indicando que ocorreu um erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }
}
