package com.netec.pedidos.client;

/**
 * El mismo error de negocio del monolito, pero ahora llega como un HTTP 409
 * desde otro proceso en lugar de subir por la pila de llamadas.
 */
public class StockInsuficienteException extends RuntimeException {
    public StockInsuficienteException(String mensaje) { super(mensaje); }
}
