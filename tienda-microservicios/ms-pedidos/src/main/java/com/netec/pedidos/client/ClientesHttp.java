package com.netec.pedidos.client;

import com.netec.pedidos.port.ClienteInfo;
import com.netec.pedidos.port.ClientesPort;
import com.netec.pedidos.port.ConsultaClientesPort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class ClientesHttp implements ClientesPort, ConsultaClientesPort {

    private static final String SERVICIO = "ms-clientes";

    private final RestClient rest;

    public ClientesHttp(@Qualifier("clientesRestClient") RestClient rest) {
        this.rest = rest;
    }

    @Override
    public ClienteInfo buscar(Long clienteId) {
        try {
            return rest.get()
                    .uri("/clientes/{id}", clienteId)
                    .retrieve()
                    .body(ClienteInfo.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RecursoNoEncontradoException("Cliente no encontrado: " + clienteId);
        } catch (ResourceAccessException e) {
            throw new ServicioNoDisponibleException(SERVICIO, e);
        }
    }

    @Override
    public List<ClienteInfo> listarClientes() {
        try {
            ClienteInfo[] clientes = rest.get()
                    .uri("/clientes")
                    .retrieve()
                    .body(ClienteInfo[].class);
            return clientes == null ? List.of() : List.of(clientes);
        } catch (ResourceAccessException e) {
            throw new ServicioNoDisponibleException(SERVICIO, e);
        }
    }
}
