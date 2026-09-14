package com.netec.productos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ms-productos: el modulo de catalogo del antiguo monolito, ahora como
 * proceso independiente con su propia base de datos y su propia API.
 */
@SpringBootApplication
public class MsProductosApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsProductosApplication.class, args);
    }
}
