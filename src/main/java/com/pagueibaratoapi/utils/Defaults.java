package com.pagueibaratoapi.utils;

import org.springframework.beans.factory.annotation.Value;

public class Defaults {

    @Value("${pagueibarato.config.token.expiration}")
    public static Integer EXPIRA_EM;

    @Value("${pagueibarato.config.token.secret.key}")
    public static String SEGREDO;
}
