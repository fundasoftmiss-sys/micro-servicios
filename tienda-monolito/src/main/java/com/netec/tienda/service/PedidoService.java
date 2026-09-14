package com.netec.tienda.service;

import com.netec.tienda.model.Cliente;
import com.netec.tienda.model.Pedido;
import com.netec.tienda.model.Producto;
import com.netec.tienda.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MOMENTO CLAVE PARA LA CLASE:
 * El modulo de pedidos llama DIRECTAMENTE (en memoria, mismo proceso) a los
 * servicios de clientes y productos. Todo ocurre en UNA transaccion sobre UNA BD.
 *
 * Ventaja: simple y consistente.
 * Desventaja: pedidos no puede desplegarse ni escalarse sin productos y clientes.
 */
@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteService clienteService;
    private final ProductoService productoService;

    public PedidoService(PedidoRepository pedidoRepository,
                         ClienteService clienteService,
                         ProductoService productoService) {
        this.pedidoRepository = pedidoRepository;
        this.clienteService = clienteService;
        this.productoService = productoService;
    }

    public List<Pedido> listar() {
        return pedidoRepository.findAll();
    }

    @Transactional
    public Pedido crear(Long clienteId, Long productoId, int cantidad) {
        Cliente cliente = clienteService.buscar(clienteId);      // llamada local
        Producto producto = productoService.buscar(productoId);  // llamada local

        productoService.descontarStock(productoId, cantidad);    // misma transaccion

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setProducto(producto);
        pedido.setCantidad(cantidad);
        pedido.setTotal(producto.getPrecio() * cantidad);
        pedido.setFecha(LocalDateTime.now());
        return pedidoRepository.save(pedido);
    }
}
