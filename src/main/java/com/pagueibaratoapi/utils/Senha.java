package com.pagueibaratoapi.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// Classe com funções úteis para a senha
public class Senha {

    // Valor do salt inicial para a senha, definido no arquivo application.properties
    @Value("${pagueibarato.config.salt.start}")
    private static String SALT_START;

    // Valor do salt final para a senha, definido no arquivo application.properties
    @Value("${pagueibarato.config.salt.end}")
    private static String SALT_END;

    // Método responsável por salgar a senha
    public static String salgarSenha(String senha){
        return SALT_START + senha + SALT_END;
    }

    public static String encriptar(String senha){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        return passwordEncoder.encode(Senha.salgarSenha(senha));
    }
}