package com.netec.pedidos.port;

/** Contrato de negocio: Pedidos solo necesita resolver un cliente por id. */
public interface ClientesPort {
    ClienteInfo buscar(Long clienteId);
}
