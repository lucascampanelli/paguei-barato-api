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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.pagueibaratoapi.services.UsuarioServiceImpl;


/**
 * Classe de configuração de segurança.
 */
@Configuration
@EnableWebSecurity
public class JWTConfig {

    // Serviço do usuário.
    private final UsuarioServiceImpl usuarioServiceImpl;
    // Gerenciador de autenticação.
    private AuthenticationManager authenticationManager;

    // Construtor.
    public JWTConfig(UsuarioServiceImpl usuarioServiceImpl) {
        this.usuarioServiceImpl = usuarioServiceImpl;
    }

    // Injeção do gerenciador de autenticação.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Injeção do criptografador de senha.
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configuração da cadeia de eventos de segurança.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Substitui o serviço de usuário padrão pelo serviço de usuário da aplicação.
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(usuarioServiceImpl);
        authenticationManager = authenticationManagerBuilder.build();

        // Desativa o cors e o csrf.
        http.cors().and().csrf().disable()

            // Habilita as requisições.
            .authorizeRequests()

            // Permite as seguintes requisições nas seguintes rotas.
            .antMatchers(HttpMethod.POST, "/usuario/").permitAll()
            .antMatchers(HttpMethod.POST, "/login").permitAll()
            .antMatchers(HttpMethod.GET, "/categoria/**").permitAll()
            .antMatchers(HttpMethod.GET, "/estoque/**").permitAll()
            .antMatchers(HttpMethod.GET, "/mercado/**").permitAll()
            .antMatchers(HttpMethod.GET, "/produto/**").permitAll()
            .antMatchers(HttpMethod.GET, "/produto/**/**").permitAll()
            .antMatchers(HttpMethod.GET, "/ramo/**").permitAll()
            .antMatchers(HttpMethod.GET, "/sugestao/**").permitAll()
            .antMatchers(HttpMethod.GET, "/").permitAll()

            // Bloqueia qualquer outra requisição de qualquer outra rota.
            .anyRequest().authenticated()

            // Define o gerênciador de autenticação.
            .and()
            .authenticationManager(authenticationManager)

            // Filtra e valida o token
            .addFilter(new JWTAuthenticationFilter(authenticationManager))
            .addFilter(new JWTValidateFilter(authenticationManager))

            // Obtém o gerenciador de sessão.
            .sessionManagement()
            // Não cria sessão para requisições feitas por um cliente (Não armazena o
            // estado)
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Retorna a configuração.
        return http.build();
    }

    // Configuração do cors.
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        // Retorna a configuração.
        return new WebMvcConfigurer() {
            // Adiciona o mapeamento de rotas e requisições.
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Permite todas as rotas.
                        .allowedMethods("*"); // Permite todos os métodos.
            }
        };
    }
}
