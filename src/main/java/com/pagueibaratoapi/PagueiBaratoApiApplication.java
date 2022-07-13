package com.pagueibaratoapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

/**
 * <H1>PagueiBaratoAPI</H1>
 * <P>API RESTful para facilitar a comparação de preços de produtos no varejo. Construída com Spring Boot.</P>
 * made with ❤ in 🇧🇷
 * @author Lucas Campanelli de Souza
 * @author Nicholas Campanelli de Souza
 * @version 1.0.0
 * @see <a href="https://github.com/lucascampanelli/paguei-barato-api">Repositório da API no GitHub</a>
 */
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
public class PagueiBaratoApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PagueiBaratoApiApplication.class, args);
	}
}
