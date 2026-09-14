package com.netec.pedidos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ms-pedidos.
 *
 * Esto ERA tienda-monolito. No se reescribio ni se borro: se le fueron
 * quitando modulos (productos, clientes) hasta que lo que quedo era,
 * exactamente, un microservicio. Eso es el patron Strangler Fig.
 */
@SpringBootApplication
public class MsPedidosApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsPedidosApplication.class, args);
    }
}
