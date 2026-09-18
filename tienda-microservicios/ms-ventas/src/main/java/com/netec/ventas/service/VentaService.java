package com.netec.ventas.service;

import com.netec.ventas.model.Venta;
import com.netec.ventas.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class VentaService {

    private final VentaRepository ventas;
    private final RestClient pedidos;

    public VentaService(VentaRepository ventas,
                        @Qualifier("pedidosRestClient") RestClient pedidos) {
        this.ventas = ventas;
        this.pedidos = pedidos;
    }

    public List<Venta> listar() {
        return ventas.findAll();
    }

    public Venta crear(Long clienteId, Long productoId, int cantidad) {
        Venta venta = new Venta();
        venta.setClienteId(clienteId);
        venta.setProductoId(productoId);
        venta.setCantidad(cantidad);
        venta.setFechaHora(LocalDateTime.now());
        venta = ventas.save(venta);

        try {
                PedidoResponse pedido = Objects.requireNonNull(pedidos.post()
                    .uri("/api/pedidos")
                    .body(Map.of(
                            "ventaId", venta.getId(),
                            "clienteId", clienteId,
                            "productoId", productoId,
                            "cantidad", cantidad,
                            "fechaHoraVenta", venta.getFechaHora()))
                    .retrieve()
                    .body(PedidoResponse.class), "ms-pedidos devolvio una respuesta vacia");

            venta.setPedidoId(pedido.id());
            return ventas.save(venta);
        } catch (RuntimeException e) {
            // La venta queda fuera si ms-pedidos no pudo completar la operación.
            ventas.deleteById(venta.getId());
            throw e;
        }
    }

    private record PedidoResponse(Long id) {}
}
