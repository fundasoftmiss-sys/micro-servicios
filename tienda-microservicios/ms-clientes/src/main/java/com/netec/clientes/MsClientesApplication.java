package com.netec.clientes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ms-clientes: el modulo de clientes del antiguo monolito, ahora independiente.
 * El mas sencillo de los tres, y por eso un buen segundo candidato a extraer.
 */
@SpringBootApplication
public class MsClientesApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsClientesApplication.class, args);
    }
}
