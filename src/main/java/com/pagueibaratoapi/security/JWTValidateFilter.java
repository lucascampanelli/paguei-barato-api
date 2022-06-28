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

public class JWTValidateFilter extends BasicAuthenticationFilter{

    @Value("${pagueibarato.config.secret.key}")
    private static String SEGREDO;

    public JWTValidateFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain chain) throws IOException, ServletException {

        String header = request.getHeader("Authorization");

        if(header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.replace("Bearer ", "");

        try {
            String usuario = JWT.require(Algorithm.HMAC256(SEGREDO))
                .build()
                .verify(token)
                .getSubject();

            if(usuario == null){
                chain.doFilter(request, response);
                return;
            }

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(usuario, null, null);
            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (Exception e) {
            response.setStatus(401);
            return;
        }
        chain.doFilter(request, response);
    }

}
