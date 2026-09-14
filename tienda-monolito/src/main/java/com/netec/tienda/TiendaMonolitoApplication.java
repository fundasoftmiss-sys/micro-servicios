package com.netec.tienda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * PUNTO DE ENTRADA UNICO.
 * Toda la aplicacion (productos, clientes, pedidos, vistas, API, base de datos)
 * arranca desde aqui como UN SOLO PROCESO. Eso es un monolito.
 */
@SpringBootApplication
public class TiendaMonolitoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TiendaMonolitoApplication.class, args);
    }
}
