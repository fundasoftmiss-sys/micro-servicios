package com.netec.productos.config;

import com.netec.productos.model.Producto;
import com.netec.productos.service.ProductoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Los mismos cuatro productos del monolito. Este servicio arranca solo:
 * no necesita que ningun otro este levantado.
 */
@Configuration
public class DatosIniciales {

    @Bean
    CommandLineRunner cargarProductos(ProductoService productos) {
        return args -> {
            productos.guardar(new Producto("Laptop", 2500.0, 10));
            productos.guardar(new Producto("Mouse", 45.0, 100));
            productos.guardar(new Producto("Teclado", 80.0, 50));
            productos.guardar(new Producto("Monitor 24 pulgadas", 650.0, 3));
            System.out.println(">> ms-productos listo en http://localhost:8081/productos");
        };
    }
}
