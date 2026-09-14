package com.netec.pedidos.port;

/**
 * Lo que ms-pedidos sabe de un producto. NO es la entidad Producto.
 *
 * La entidad JPA se queda en ms-productos; por la red viaja este DTO. Asi
 * los dos servicios pueden cambiar su modelo de datos por separado.
 */
public record ProductoInfo(Long id, String nombre, double precio, int stock) {}
