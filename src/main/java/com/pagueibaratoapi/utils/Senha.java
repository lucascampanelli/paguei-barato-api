package com.pagueibaratoapi.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Classe com funções úteis para a senha
 */
public class Senha {

    // Valor do salt inicial para a senha, definido no arquivo application.properties
    @Value("${pagueibarato.config.salt.start}")
    private static String SALT_START;

    // Valor do salt final para a senha, definido no arquivo application.properties
    @Value("${pagueibarato.config.salt.end}")
    private static String SALT_END;

    /**
     * Método responsável por <b>salgar</b> uma senha
     * @param senha - Senha a ser salgada
     * @return String - Senha salgada
    */ 
    public static String salgar(String senha) {
        // Adiciona um salt fixo no início e outro no fim da senha informada
        return SALT_START + senha + SALT_END;
    }

    /**
     * Método responsável por <b>encriptar</b> uma senha
     * @param senha - Senha limpa a ser encriptada
     * @return String - Senha criptografada
     */
    public static String encriptar(String senha) {
        // Cria um novo objeto do tipo BCryptPasswordEncoder
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // Retorna a senha salgada, na sua forma limpa, e criptografada
        return passwordEncoder.encode(Senha.salgar(senha));
    }

    /**
     * Método responsável por <b>verificar</b> se a senha limpa é a mesma que a senha criptografada
     * @param senha - Senha em sua forma limpa
     * @param senhaCriptografada - Senha criptografada
     * @return boolean - true se a senha for igual a senha criptografada, false se não for
     */
    public static boolean validar(String senha, String senhaCriptografada) {
        // Cria um novo objeto do tipo BCryptPasswordEncoder
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // Retorna true se a senha informada for igual à senha criptografada ou false se não for
        return passwordEncoder.matches(Senha.salgar(senha), senhaCriptografada);
    }
}