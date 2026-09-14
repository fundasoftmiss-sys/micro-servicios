package com.netec.productos.web;

/** Cuerpo de POST /productos/reservas */
public record ReservaRequest(String opId, Long productoId, int cantidad) {}
