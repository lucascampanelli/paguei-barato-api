package com.pagueibaratoapi.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

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
import com.pagueibaratoapi.models.requests.Usuario;
import com.pagueibaratoapi.models.responses.ResponseUsuario;
import com.pagueibaratoapi.repository.UsuarioRepository;
import com.pagueibaratoapi.utils.EditaRecurso;
import com.pagueibaratoapi.utils.Senha;
import com.pagueibaratoapi.utils.Tratamento;

/**
 * Classe responsável por controlar as requisições dos usuários
 */
@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    // Iniciando a variável de instância do repositório
    private final UsuarioRepository usuarioRepository;

    // Construtor do controller do usuário, que realizará a injeção de dependência do repositório
    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Método responsável por criar um novo usuário
     * @param requestUsuario - Objeto que contém os dados do novo usuário.
     * @return ResponseUsuario - Objeto que contém o usuário que foi criado.
     */
    @PostMapping
    public ResponseUsuario criar(@RequestBody Usuario requestUsuario) {
        try {

            // Validando os dados enviados pelo cliente por parâmetro.
            Tratamento.validarUsuario(requestUsuario, false);

            // Verifica se existe um usuário com o email informado pelo cliente.
            if(usuarioRepository.findByEmail(requestUsuario.getEmail()) != null)
                throw new DadosConflitantesException("email_em_uso");

            // Setando a senha do usuário que será criado como a senha enviada pelo cliente criptografada.
            requestUsuario.setSenha(Senha.encriptar(requestUsuario.getSenha()));

            // Salvando o usuário no banco de dados e armazenando no objeto de resposta ResponseUsuario.
            ResponseUsuario responseUsuario = new ResponseUsuario(usuarioRepository.save(requestUsuario));

            // Adicionando à resposta o link para leitura usuário criado.
            responseUsuario.add(
                linkTo(
                    methodOn(UsuarioController.class).ler(responseUsuario.getId())
                )
                .withSelfRel()
            );

            // Retornando o objeto de resposta.
            return responseUsuario;

        } catch (DadosConflitantesException e) {
            // Lançando exceção informando que os dados enviados por parâmetro são conflitantes com os dados do banco de dados.
            throw new ResponseStatusException(409, e.getMessage(), e);
        } catch (DadosInvalidosException e) {
            // Lançando exceção informando que os dados enviados por parâmetro são inválidos.
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (DataIntegrityViolationException e) {
            // Lançando exceção informando que há violação de integridade de dados.
            throw new ResponseStatusException(500, "erro_insercao", e);
        } catch (IllegalArgumentException e) {
            // Lançando exceção informando que os dados enviados por parâmetro são inválidos.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (Exception e) {
            // Lançando exceção informando que há um erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Método responsável por ler um usuário com o id informado
     * @param id - Id do usuário que será lido.
     * @return ResponseUsuario - Objeto que contém o usuário que foi lido.
     */
    @GetMapping("/{id}")
    public ResponseUsuario ler(@PathVariable("id") Integer id) {
        try {

            // Buscando o usuário com o id informado.
            Usuario usuarioEncontrado = usuarioRepository.findById(id).get();

            // Verificando se o usuário encontrado não foi removido.
            // Quando um usuário é removido, ele tem seus atributos setados como vazio (aspas vazias).
            if(!Tratamento.usuarioExiste(usuarioEncontrado))
                // Se o usuário não existe, lançando exceção informando que o usuário não existe.
                throw new NoSuchElementException("usuario_nao_encontrado");

            // Armazenando o usuário encontrado no objeto de resposta ResponseUsuario.
            ResponseUsuario responseUsuario = new ResponseUsuario(usuarioEncontrado);

            // Adicionando à resposta o link para listagem usuário criado.
            if(responseUsuario != null) {
                responseUsuario.add(
                    linkTo(
                        methodOn(UsuarioController.class).listar()
                    )
                    .withRel("collection")
                );
            }

            // Retornando o objeto de resposta do usuário encontrado.
            return responseUsuario;

        } catch (NoSuchElementException e) {
            // Lançando exceção informando que o usuário não foi encontrado.
            throw new ResponseStatusException(404, "usuario_nao_encontrado", e);
        } catch (Exception e) {
            // Lançando exceção informando que há um erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Método responsável por listar todos os usuários
     * @return <b>List<ResponseUsuario></b> - Lista de objetos que contém todos os usuários encontrados.
     */
    @GetMapping
    public List<ResponseUsuario> listar() {
        try {
            // Buscando todos os usuários e armazenando numa lista de usuários
            List<Usuario> usuarios = usuarioRepository.findAll();

            // Criando uma lista de resposta do tipo ResponseUsuario vazia.
            List<ResponseUsuario> responseUsuario = new ArrayList<ResponseUsuario>();

            // Para cada usuário da lista de usuarios
            for(Usuario usuario : usuarios) {
                // Convertendo o usuário para um objeto de resposta ResponseUsuario e adicionando à lista de resposta.
                responseUsuario.add(new ResponseUsuario(usuario));
            }

            // Se a lista de resposta possuir algum usuário
            if(!responseUsuario.isEmpty()) {
                // Para cada usuário da lista de resposta
                for(ResponseUsuario usuario : responseUsuario) {
                    // Adiciona ao objeto um link para a leitura do usuário em questão
                    usuario.add(
                        linkTo(
                            methodOn(UsuarioController.class).ler(usuario.getId())
                        )
                        .withSelfRel()
                    );
                }
            }

            // Retornando a lista de resposta de usuários.
            return responseUsuario;

        } catch (NullPointerException e) {
            // Lançando exceção informando que algum registro não foi encontrado.
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (UnsupportedOperationException e) {
            // Lançando exceção informando que há um erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (Exception e) {
            // Lançando exceção informando que há um erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Método responsável por atualizar atributos específicos de um usuário com o id informado.
     * @param id - Id do usuário que será atualizado.
     * @param requestUsuario - Objeto que contém os dados do usuário que será atualizado.
     * @return ResponseUsuario - Mercado atualizado.
     */
    @PatchMapping("/{id}")
    public ResponseUsuario editar(@PathVariable("id") Integer id, @RequestBody Usuario requestUsuario) {
        try {

            // Validando os parâmetros enviados pelo cliente
            Tratamento.validarUsuario(requestUsuario, true);

            // Verificando se já existe um usuário com o email informado
            if(usuarioRepository.findByEmail(requestUsuario.getEmail()) != null)
                // Lançando exceção de conflito informando que já existe um usuário com o email informado.
                throw new DadosConflitantesException("email_em_uso");

            // Buscando o estado atual usuário com o id informado e armazenando no objeto de resposta ResponseUsuario.
            Usuario usuarioAtual = usuarioRepository.findById(id).get();

            // Atualizando o usuário e armazenando num objeto de resposta ResponseUsuario.
            ResponseUsuario responseUsuario = new ResponseUsuario(
                usuarioRepository.save(
                    // Adicionando as alterações enviadas pelo cliente e mantendo as demais conforme o estado atual.
                    EditaRecurso.editarUsuario(
                        usuarioAtual, 
                        requestUsuario
                    )
                )
            );
            
            // Adicionando à resposta o link para leitura do usuário criado.
            responseUsuario.add(
                linkTo(
                    methodOn(UsuarioController.class).ler(responseUsuario.getId())
                )
                .withSelfRel()
            );

            // Retornando o objeto de resposta do usuário atualizado.
            return responseUsuario;

        } catch (DadosConflitantesException e) {
            // Lançando uma exceção informando que há um conflito dos dados enviados com os dados do banco de dados.
            throw new ResponseStatusException(409, e.getMessage(), e);
        } catch (DadosInvalidosException e) {
            // Lançando uma exceção informando que os dados enviados por parâmetro são inválidos.
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (DataIntegrityViolationException e) {
            // Lançando uma exceção informando que há uma violação na integridade de dados.
            throw new ResponseStatusException(500, "erro_insercao", e);
        } catch (IllegalArgumentException e) {
            // Lançando uma exceção informando que os dados enviados são inválidos.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (NoSuchElementException e) {
            // Lançando uma exceção informando que o registro não foi encontrado.
            throw new ResponseStatusException(404, "nao_encontrado", e);
        } catch (Exception e) {
            // Lançando uma exceção informando que há um erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Método responsável por atualizar todos os atributos de um usuário com o id informado.
     * @param id - Id do usuário que será atualizado.
     * @param requestUsuario - Objeto que contém os dados do usuário que será atualizado.
     * @return ResponseUsuario - Mercado atualizado.
     */
    @PutMapping("/{id}")
    public ResponseUsuario atualizar(@PathVariable("id") Integer id, @RequestBody Usuario requestUsuario) {
        try {

            // Validando os parâmetros enviados pelo cliente.
            Tratamento.validarUsuario(requestUsuario, false);

            // Verificando se já existe um usuário com o email informado pelo cliente.
            if(usuarioRepository.findByEmail(requestUsuario.getEmail()) != null)
                // Lançando uma exceção informando que já existe um usuário com o email informado.
                throw new DadosConflitantesException("email_em_uso");

            // Adicionando ao corpo da requisição o id do recurso que será atualizado.
            requestUsuario.setId(id);

            // Setando a senha que será atualizada como a senha enviada pelo cliente com a criptografia.
            requestUsuario.setSenha(Senha.encriptar(requestUsuario.getSenha()));

            // Atualizando o usuário e armazenando o estado atualizado na variável de resposta.
            ResponseUsuario responseUsuario = new ResponseUsuario(usuarioRepository.save(requestUsuario));

            // Adicionando à resposta o link para leitura do usuário atualizado.
            responseUsuario.add(
                linkTo(
                    methodOn(UsuarioController.class).ler(responseUsuario.getId())
                )
                .withSelfRel()
            );

            // Retornando o objeto de resposta com o usuário atualizado.
            return responseUsuario;

        } catch (DadosConflitantesException e) {
            // Lançando uma exceção informando que os dados do cliente são conflitantes com os dados do banco de dados.
            throw new ResponseStatusException(409, e.getMessage(), e);
        } catch (DadosInvalidosException e) {
            // Lançando uma exceção informando que os dados enviados pelo cliente são inválidos.
            throw new ResponseStatusException(400, e.getMessage(), e);
        } catch (NoSuchElementException e) {
            // Lançando uma exceção informando que o registro solicitado não existente.
            throw new ResponseStatusException(404, e.getMessage(), e);
        } catch (DataIntegrityViolationException e) {
            // Lançando uma exceção informando que há uma violação na integridade dos dados.
            throw new ResponseStatusException(500, "erro_insercao", e);
        } catch (IllegalArgumentException e) {
            // Lançando uma exceção informando que os dados informados pelo cliente são inválidos.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (Exception e) {
            // Lançando uma exceção informando que ocorreu algum erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }

    /**
     * Método responsável por deletar um usuário com o id informado.
     * @param id - Id do usuário que será removido.
     * @return Object - Link para a listagem de usuários.
     */
    @DeleteMapping("/{id}")
    public Object remover(@PathVariable int id) {
        try {

            // Buscando o usuário que será removido e armazenando numa instância de usuário.
            Usuario usuarioDeletado = usuarioRepository.findById(id).get();

            /* 
            * Setando todos os atributos do usuário como vazio,
            * haja vista que o usuário será mantido para manter integridade de outros recursos atrelados ao usuário,
            * mesmo que este não exista mais.
            */
            usuarioDeletado.setId(id);
            usuarioDeletado.setNome("");
            usuarioDeletado.setEmail("");
            usuarioDeletado.setSenha("");
            usuarioDeletado.setLogradouro("");
            usuarioDeletado.setNumero(-1);
            usuarioDeletado.setComplemento(null);
            usuarioDeletado.setBairro("");
            usuarioDeletado.setCidade("");
            usuarioDeletado.setUf("--");
            usuarioDeletado.setCep("00000-000");

            // Atualizando o usuário.
            usuarioRepository.save(usuarioDeletado);

            // Retornando um link para a listagem de todos os usuários.
            return linkTo(
                        methodOn(UsuarioController.class).listar()
                    )
                    .withRel("collection");

        } catch (NoSuchElementException e) {
            // Lançando uma exceção informando que o registro informado não foi encontrado.
            throw new ResponseStatusException(404, e.getMessage(), e);
        } catch (DataIntegrityViolationException e) {
            // Lançando uma exceção informando que há uma violação na integridade dos dados.
            throw new ResponseStatusException(500, "erro_remocao", e);
        } catch (IllegalArgumentException e) {
            // Lançando uma exceção informado que os dados enviados pelo usuário são inválidos.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        } catch (Exception e) {
            // Lançando uma exceção informando que ocorreu um erro inesperado.
            throw new ResponseStatusException(500, "erro_inesperado", e);
        }
    }
}
