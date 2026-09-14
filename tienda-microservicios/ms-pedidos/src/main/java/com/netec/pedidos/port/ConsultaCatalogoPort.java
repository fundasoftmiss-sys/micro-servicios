package com.netec.pedidos.port;

import java.util.List;

/**
 * OJO: esto NO es una dependencia de negocio.
 *
 * La vista de este servicio tiene un formulario con un desplegable de
 * productos, y para pintarlo hace falta el catalogo. Es una lectura de
 * interfaz de usuario, no una regla de ventas, y por eso vive en un contrato
 * aparte del de negocio.
 *
 * En un sistema mayor esta composicion no estaria dentro de ms-pedidos:
 * estaria en un BFF o en el API Gateway. Aqui esta para que la demo tenga
 * un formulario con el que jugar en clase.
 */
public interface ConsultaCatalogoPort {
    List<ProductoInfo> listarProductos();
}
