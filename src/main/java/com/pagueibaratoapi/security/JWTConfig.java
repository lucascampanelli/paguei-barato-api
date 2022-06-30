package com.pagueibaratoapi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.pagueibaratoapi.services.UsuarioServiceImpl;

// Configuração do token JWT
public class JWTConfig {
    
    private final UsuarioServiceImpl usuarioServiceImpl;
    private AuthenticationManager authenticationManager;

    public JWTConfig(UsuarioServiceImpl usuarioServiceImpl) {
        this.usuarioServiceImpl = usuarioServiceImpl;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(usuarioServiceImpl);
        authenticationManager = authenticationManagerBuilder.build();

        http.cors().and().csrf().disable()
                    .sessionManagement()
                    // Não cria sessão para requisições feitas por um cliente (Não armazena o estado)
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                    .and()
                    
                    .authorizeRequests()

                    // Permite as seguintes requisições
                    .antMatchers(HttpMethod.POST, "/usuario").permitAll()
                    .antMatchers(HttpMethod.POST, "/login").permitAll()
                    .antMatchers(HttpMethod.GET, "/categoria").permitAll()
                    .antMatchers(HttpMethod.GET, "/estoque").permitAll()
                    .antMatchers(HttpMethod.GET, "/mercado").permitAll()
                    .antMatchers(HttpMethod.GET, "/produto").permitAll()
                    .antMatchers(HttpMethod.GET, "/ramo").permitAll()
                    .antMatchers(HttpMethod.GET, "/sugestao").permitAll()

                    // Bloqueia qualquer outra requisição
                    .anyRequest().authenticated()

                    .and()

                    // Filtra o token
                    .addFilter(new JWTAuthenticationFilter(authenticationManager))
                    .addFilter(new JWTValidateFilter(authenticationManager));

        return http.build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("*");
            }
        };
    }
}
