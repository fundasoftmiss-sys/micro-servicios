package com.netec.pedidos.config;

import com.netec.pedidos.service.PedidoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OTRA DIFERENCIA QUE MERECE LA PENA SENNALAR EN CLASE.
 *
 * El DatosIniciales del monolito siempre funcionaba: creaba productos,
 * clientes y pedidos de un tirón porque todo vivia en el mismo proceso.
 *
 * Este no puede prometer lo mismo. Para crear un pedido necesita que
 * ms-productos y ms-clientes ya esten levantados, y al arrancar puede que
 * no lo esten. En vez de reventar el arranque, avisa y sigue: un servicio
 * nunca debe negarse a arrancar porque otro no este listo.
 */
@Configuration
@ConditionalOnProperty(name = "datos.iniciales", havingValue = "true")
public class DatosIniciales {

    @Bean
    CommandLineRunner cargarPedidos(PedidoService pedidos) {
        return args -> {
            try {
                pedidos.crear(1L, 1L, 1);   // Ana Torres  -> Laptop
                pedidos.crear(2L, 2L, 2);   // Luis Gomez  -> Mouse
                System.out.println(">> ms-pedidos listo en http://localhost:8080");
            } catch (RuntimeException e) {
                System.out.println();
                System.out.println("   ms-pedidos ARRANCO, pero sin pedidos de ejemplo.");
                System.out.println("   Motivo: " + e.getMessage());
                System.out.println("   Levanta ms-productos (8081) y ms-clientes (8082) y crea");
                System.out.println("   los pedidos desde http://localhost:8080/pedidos");
                System.out.println();
            }
        };
    }
}
