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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.pagueibaratoapi.services.UsuarioService;

@Configuration
@EnableWebSecurity
public class JWTConfig {
    
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;

    public JWTConfig(UsuarioService usuarioService, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(usuarioService);
        authenticationManager = authenticationManagerBuilder.build();

        http.cors().and().csrf().disable().authorizeRequests()
                    .antMatchers(HttpMethod.POST, "/usuario").permitAll()
                    .antMatchers(HttpMethod.POST, "/login").permitAll()
                    .and()
                    .addFilter(new JWTAuthenticationFilter(authenticationManager))
                    .addFilter(new JWTValidateFilter(authenticationManager))
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

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
