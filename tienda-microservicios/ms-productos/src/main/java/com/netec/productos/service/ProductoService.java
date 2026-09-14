package com.netec.productos.service;

import com.netec.productos.model.Producto;
import com.netec.productos.model.Reserva;
import com.netec.productos.repository.ProductoRepository;
import com.netec.productos.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productos;
    private final ReservaRepository reservas;

    public ProductoService(ProductoRepository productos, ReservaRepository reservas) {
        this.productos = productos;
        this.reservas = reservas;
    }

    public List<Producto> listar() {
        return productos.findAll();
    }

    public Producto buscar(Long id) {
        return productos.findById(id)
                .orElseThrow(() -> new NoEncontradoException("Producto no encontrado: " + id));
    }

    public Producto guardar(Producto producto) {
        return productos.save(producto);
    }

    /**
     * PASO 1 DE LA SAGA. Descuenta el stock y deja constancia de la reserva.
     *
     * La transaccion cubre las dos escrituras porque ambas viven en ESTA base
     * de datos. Lo que ya no cubre es el pedido, que esta en otro servicio.
     *
     * Es idempotente: si llega dos veces el mismo opId, la segunda vez
     * devuelve la reserva existente sin descontar de nuevo.
     */
    @Transactional
    public Reserva reservar(String opId, Long productoId, int cantidad) {
        Reserva existente = reservas.findById(opId).orElse(null);
        if (existente != null) {
            return existente;                     // ya procesada: no repetimos
        }

        Producto producto = buscar(productoId);
        if (producto.getStock() < cantidad) {
            throw new StockInsuficienteException("Stock insuficiente para " + producto.getNombre()
                    + " (disponible: " + producto.getStock() + ", pedido: " + cantidad + ")");
        }

        producto.setStock(producto.getStock() - cantidad);
        productos.save(producto);

        return reservas.save(new Reserva(opId, productoId, cantidad));
    }

    /** PASO 2 DE LA SAGA. El pedido se guardo bien: la reserva pasa a definitiva. */
    @Transactional
    public Reserva confirmar(String opId) {
        Reserva reserva = reservas.findById(opId)
                .orElseThrow(() -> new NoEncontradoException("Reserva no encontrada: " + opId));

        if (reserva.getEstado() == Reserva.Estado.LIBERADA) {
            throw new IllegalStateException("La reserva " + opId + " ya fue liberada");
        }
        reserva.setEstado(Reserva.Estado.CONFIRMADA);
        return reservas.save(reserva);
    }

    /**
     * COMPENSACION. Devuelve el stock reservado.
     *
     * Esto es lo que en el monolito hacia gratis el rollback de la transaccion.
     * Aqui hay que escribirlo, probarlo y vigilar que se ejecute.
     */
    @Transactional
    public Reserva liberar(String opId) {
        Reserva reserva = reservas.findById(opId)
                .orElseThrow(() -> new NoEncontradoException("Reserva no encontrada: " + opId));

        if (reserva.getEstado() == Reserva.Estado.LIBERADA) {
            return reserva;                       // idempotente
        }

        Producto producto = buscar(reserva.getProductoId());
        producto.setStock(producto.getStock() + reserva.getCantidad());
        productos.save(producto);

        reserva.setEstado(Reserva.Estado.LIBERADA);
        return reservas.save(reserva);
    }

    public List<Reserva> listarReservas() {
        return reservas.findAll();
    }
}
