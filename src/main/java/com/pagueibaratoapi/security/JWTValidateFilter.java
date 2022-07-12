package com.pagueibaratoapi.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

// Classe para validação do token JWT estendendo a classe BasicAuthenticationFilter, que filta o JWT
public class JWTValidateFilter extends BasicAuthenticationFilter {

    // Chave secreta para gerar o token.
    @Value("${pagueibarato.config.token.secret.key}")
    private static String SEGREDO = "shhh";

    // Construtor.
    public JWTValidateFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    // Inclui uma etapa na cadeia de eventos de autenticação.
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain
    ) throws IOException, ServletException {

        // Pega o valor do token do header.
        String header = request.getHeader("Authorization");

        // Se o token não for encontrado, continua a cadeia de eventos.
        if(header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        // Remove o prefixo do cabeçalho do token
        String token = header.replace("Bearer ", "");

        // Chama a função para validar o token.
        UsernamePasswordAuthenticationToken authenticationToken = getAuthenticationToken(token);
        // Guarda o token no contexto de segurança do Spring.
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // Continua a cadeia de eventos.
        chain.doFilter(request, response);
    }

    /**
     * Método para validar o token JWT de autenticação.
     * @param token - Token JWT.
     * @return - Token JWT autenticado.
     */
    private UsernamePasswordAuthenticationToken getAuthenticationToken(String token) {
        // Valida o token com a chave secreta.
        String usuario = JWT.require(Algorithm.HMAC512(SEGREDO))
            .build()
            .verify(token)
            .getSubject();

        // Se o token não resultar em um usuário, retorna null.
        if(usuario == null)
            return null;

        // Retorna o token JWT autenticado.
        return new UsernamePasswordAuthenticationToken(usuario, null, null);
    }
}