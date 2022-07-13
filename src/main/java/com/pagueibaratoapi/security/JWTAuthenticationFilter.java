package com.pagueibaratoapi.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pagueibaratoapi.data.UsuarioService;
import com.pagueibaratoapi.models.requests.Usuario;
import com.pagueibaratoapi.utils.Senha;

/**
 * Filtro de autenticação para o JWT.
 */
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    // Tempo de expiração do token.
    private final long EXPIRA_EM;

    // Chave secreta para gerar o token.
    private final String SEGREDO;

    // Gerenciador de autenticação.
    private final AuthenticationManager authenticationManager;

    // Construtor.
    public JWTAuthenticationFilter(
        @Value("${pagueibarato.config.token.expiration}") long expiraEm,
        @Value("${pagueibarato.config.token.secret.key}") String segredo,
        AuthenticationManager authenticationManager
    ) {
        this.EXPIRA_EM = expiraEm;
        this.SEGREDO = segredo;
        this.authenticationManager = authenticationManager;
    }

    // Método chamado para realizar autenticação ao acessar a rota /login.
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {

            // Lê o corpo da requisição e transforma em modelo de usuário.
            Usuario usuario = new ObjectMapper().readValue(request.getInputStream(), Usuario.class);

            // Realiza o tempeiro da senha,
            // Cria um token com a senha tempeirada e email do usuário,
            // Tenta realizar autenticação com o token por meio do authenticationManager.
            return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    usuario.getEmail(),
                    Senha.salgar(usuario.getSenha())
                )
            );

        } catch (BadCredentialsException e) {
            throw new RuntimeException("Senha inválida.");
        } catch (StreamReadException e) {
            e.printStackTrace();
            throw new RuntimeException("Falha ao autenticar o usuário: ");
        } catch (DatabindException e) {
            e.printStackTrace();
            throw new RuntimeException("Falha ao autenticar o usuário");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Falha ao autenticar o usuário");
        }
    }

    // Método chamado quando a autenticação é bem-sucedida.
    @Override
    protected void successfulAuthentication(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filter,
        Authentication authResult
    ) throws IOException, ServletException {

        // Obtém o objeto com as credenciais do usuário autenticado e converte para o serviço do usuário.
        UsuarioService usuarioService = (UsuarioService) authResult.getPrincipal();

        // Gera um token JWT com o email do usuário e o tempo de expiração.
        String token = JWT.create()
            .withSubject(usuarioService.getUsername())
            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRA_EM))
            .sign(Algorithm.HMAC512(SEGREDO));

        // Escreve o token na resposta e envia.
        response.getWriter().write(token);
        response.getWriter().flush();
    }
}
