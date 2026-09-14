package com.netec.tienda.config;

import com.netec.tienda.model.Cliente;
import com.netec.tienda.model.Producto;
import com.netec.tienda.service.ClienteService;
import com.netec.tienda.service.PedidoService;
import com.netec.tienda.service.ProductoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Carga datos de ejemplo al arrancar para que la demo no empiece vacia. */
@Configuration
public class DatosIniciales {

    @Bean
    CommandLineRunner cargarDatos(ProductoService productos, ClienteService clientes, PedidoService pedidos) {
        return args -> {
            Producto laptop = productos.guardar(new Producto("Laptop", 2500.0, 10));
            Producto mouse = productos.guardar(new Producto("Mouse", 45.0, 100));
            productos.guardar(new Producto("Teclado", 80.0, 50));
            productos.guardar(new Producto("Monitor 24 pulgadas", 650.0, 3));

            Cliente ana = clientes.guardar(new Cliente("Ana Torres", "ana@correo.com"));
            Cliente luis = clientes.guardar(new Cliente("Luis Gomez", "luis@correo.com"));

            pedidos.crear(ana.getId(), laptop.getId(), 1);
            pedidos.crear(luis.getId(), mouse.getId(), 2);
        };
    }
}
