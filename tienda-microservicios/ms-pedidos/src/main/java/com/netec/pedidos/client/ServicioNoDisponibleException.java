package com.netec.pedidos.client;

/**
 * Un fallo que en el monolito era IMPOSIBLE.
 *
 * Una llamada de metodo no puede "no responder". Una llamada HTTP si:
 * el servicio esta caido, la red se perdio o el timeout salto.
 */
public class ServicioNoDisponibleException extends RuntimeException {

    private final String servicio;

    public ServicioNoDisponibleException(String servicio, Throwable causa) {
        super("El servicio " + servicio + " no responde. Comprueba que este levantado.", causa);
        this.servicio = servicio;
    }

    public String getServicio() { return servicio; }
}
