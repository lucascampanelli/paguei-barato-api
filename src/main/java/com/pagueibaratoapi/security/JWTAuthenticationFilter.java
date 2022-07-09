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

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Value("${pagueibarato.config.token.expiration}")
    private long EXPIRA_EM = 2592000000L;

    @Value("${pagueibarato.config.token.secret.key}")
    private String SEGREDO = "shhh";

    private final AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {

            Usuario usuario = new ObjectMapper().readValue(request.getInputStream(), Usuario.class);

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

    @Override
    protected void successfulAuthentication(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filter,
        Authentication authResult
    ) throws IOException, ServletException {

        UsuarioService usuarioService = (UsuarioService) authResult.getPrincipal();

        String token = JWT.create()
            .withSubject(usuarioService.getUsername())
            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRA_EM))
            .sign(Algorithm.HMAC512(SEGREDO));

        response.getWriter().write(token);
        response.getWriter().flush();
    }
}
