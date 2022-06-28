package com.pagueibaratoapi.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pagueibaratoapi.models.Usuario;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter{
    
    @Value("${pagueibarato.config.token.expiration")
    private static int EXPIRA_EM;

    @Value("${pagueibarato.config.token.expiration")
    private static String SEGREDO;

    private final AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public Authentication attemptAuthentication(HttpServletRequest request, 
                                                HttpServletResponse response) throws AuthenticationException {

        ObjectMapper mapper = new ObjectMapper();
        
        try {

            Usuario usuario = mapper.readValue(request.getInputStream(), Usuario.class);

            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(usuario.getEmail(), usuario.getSenha()));

        } catch (StreamReadException e) {
            throw new RuntimeException("Erro ao ler o JSON do usuário");
        } catch (DatabindException e) {
            throw new RuntimeException("Erro ao ler o JSON do usuário");
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler o JSON do usuário");
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, 
                                            FilterChain filter, Authentication authResult) throws IOException, ServletException {
        
        Usuario usuario = (Usuario) authResult.getPrincipal();
        String token = JWT.create()
                .withSubject(usuario.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRA_EM))
                .sign(Algorithm.HMAC512(SEGREDO));

        response.getWriter().write(token);
        response.getWriter().flush();
    }
}
