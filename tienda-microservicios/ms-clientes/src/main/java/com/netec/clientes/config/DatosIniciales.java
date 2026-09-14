package com.netec.clientes.config;

import com.netec.clientes.model.Cliente;
import com.netec.clientes.service.ClienteService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatosIniciales {

    @Bean
    CommandLineRunner cargarClientes(ClienteService clientes) {
        return args -> {
            clientes.guardar(new Cliente("Ana Torres", "ana@correo.com"));
            clientes.guardar(new Cliente("Luis Gomez", "luis@correo.com"));
            System.out.println(">> ms-clientes listo en http://localhost:8082/clientes");
        };
    }
}
