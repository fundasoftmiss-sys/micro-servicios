package com.netec.pedidos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

/**
 * COMPARA ESTO CON EL Pedido DEL MONOLITO.
 *
 * Antes:  @ManyToOne private Cliente cliente;      -> llave foranea real
 *         @ManyToOne private Producto producto;    -> llave foranea real
 *
 * Ahora:  solo los identificadores, mas COPIAS HISTORICAS del nombre y del
 *         precio. No es duplicacion accidental, es el requisito: un pedido
 *         es un documento de lo que ocurrio, no una vista de los datos de hoy.
 *         Si manana sube el precio del Monitor, este pedido debe seguir
 *         mostrando lo que se cobro.
 *
 * Efecto secundario que se agradece: esta pantalla sigue funcionando aunque
 * ms-productos y ms-clientes esten caidos.
 */
@Entity
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long clienteId;
    private String clienteNombre;      // copia historica

    private Long productoId;
    private String productoNombre;     // copia historica
    private double precioUnitario;     // copia historica

    private int cantidad;
    private double total;
    private LocalDateTime fecha;

    public Pedido() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }
    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }
    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
