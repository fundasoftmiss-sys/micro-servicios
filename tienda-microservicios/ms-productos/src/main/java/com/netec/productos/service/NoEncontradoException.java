package com.netec.productos.service;

public class NoEncontradoException extends RuntimeException {
    public NoEncontradoException(String mensaje) { super(mensaje); }
}
