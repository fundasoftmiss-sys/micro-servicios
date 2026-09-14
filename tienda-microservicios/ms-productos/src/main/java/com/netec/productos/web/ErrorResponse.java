package com.netec.productos.web;

/** Cuerpo de error que devuelve la API. ms-pedidos lo lee para mostrar el motivo real. */
public record ErrorResponse(String mensaje) {}
