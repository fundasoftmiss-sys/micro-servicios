package com.netec.productos.web;

import com.netec.productos.service.NoEncontradoException;
import com.netec.productos.service.StockInsuficienteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * En el monolito, una excepcion viajaba por la pila de llamadas hasta el
 * controlador. Aqui hay que TRADUCIRLA a un codigo HTTP para que el otro
 * servicio pueda distinguir "no existe" de "no hay stock" de "estoy caido".
 */
@RestControllerAdvice
public class ManejadorDeErrores {

    @ExceptionHandler(NoEncontradoException.class)
    public ResponseEntity<ErrorResponse> noEncontrado(NoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<ErrorResponse> sinStock(StockInsuficienteException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> estadoInvalido(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
    }
}
