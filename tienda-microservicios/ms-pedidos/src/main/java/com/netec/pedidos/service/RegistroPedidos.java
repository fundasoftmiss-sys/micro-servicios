package com.netec.pedidos.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.netec.pedidos.model.Pedido;
import com.netec.pedidos.port.ClienteInfo;
import com.netec.pedidos.port.ProductoInfo;
import com.netec.pedidos.repository.PedidoRepository;

/**
 * La transaccion SOBREVIVIO a la migracion, pero encogio: ahora solo cubre
 * lo que vive en la base de datos de ESTE servicio.
 *
 * Esta en una clase aparte a proposito. Si @Transactional estuviera sobre un
 * metodo privado de PedidoService, Spring no lo interceptaria (el proxy solo
 * envuelve llamadas que entran desde fuera del bean) y la anotacion no haria
 * nada. Es un fallo clasico y silencioso.
 *
 * Y sobre todo: la transaccion NO puede envolver a crear(), porque crear()
 * hace llamadas de red. Mantener abierta una transaccion de base de datos
 * mientras esperas a otro servicio es una de las peores cosas que se pueden
 * hacer en un sistema distribuido.
 */
@Service
public class RegistroPedidos {

    private final PedidoRepository pedidos;

    public RegistroPedidos(PedidoRepository pedidos) {
        this.pedidos = pedidos;
    }

    @Transactional
    public Pedido guardar(ClienteInfo cliente, ProductoInfo producto, int cantidad) {
        return guardar(cliente, producto, cantidad, null, null);
    }

    @Transactional
    public Pedido guardar(ClienteInfo cliente, ProductoInfo producto, int cantidad,
                           Long ventaId, LocalDateTime fechaHoraVenta) {
        Pedido pedido = new Pedido();

        pedido.setVentaId(ventaId);
        pedido.setFechaHoraVenta(fechaHoraVenta);

        pedido.setClienteId(cliente.id());
        pedido.setClienteNombre(cliente.nombre());        // copia historica

        pedido.setProductoId(producto.id());
        pedido.setProductoNombre(producto.nombre());      // copia historica
        pedido.setPrecioUnitario(producto.precio());      // copia historica

        pedido.setCantidad(cantidad);
        pedido.setTotal(producto.precio() * cantidad);
        pedido.setFecha(LocalDateTime.now());

        return pedidos.save(pedido);
    }
}
