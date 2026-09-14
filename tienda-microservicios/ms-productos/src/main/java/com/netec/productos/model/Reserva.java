package com.netec.productos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

/**
 * ESTA CLASE NO EXISTIA EN EL MONOLITO.
 *
 * Aparece porque el descuento de stock ya no puede ocurrir dentro de la
 * transaccion de quien crea el pedido: ahora son dos bases de datos
 * distintas. La reserva es el estado intermedio que hace posible la saga
 * (reservar -> confirmar, o reservar -> liberar).
 *
 * La clave primaria es el opId que genera ms-pedidos: si la red entrega
 * dos veces la misma peticion, la segunda encuentra la reserva ya creada
 * y no vuelve a descontar. Eso es idempotencia.
 */
@Entity
public class Reserva {

    public enum Estado { RESERVADA, CONFIRMADA, LIBERADA }

    @Id
    private String opId;

    private Long productoId;
    private int cantidad;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    private LocalDateTime fecha;

    public Reserva() {}

    public Reserva(String opId, Long productoId, int cantidad) {
        this.opId = opId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.estado = Estado.RESERVADA;
        this.fecha = LocalDateTime.now();
    }

    public String getOpId() { return opId; }
    public void setOpId(String opId) { this.opId = opId; }
    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
