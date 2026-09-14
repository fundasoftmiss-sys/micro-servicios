package com.netec.productos.service;

public class StockInsuficienteException extends RuntimeException {
    public StockInsuficienteException(String mensaje) { super(mensaje); }
}
