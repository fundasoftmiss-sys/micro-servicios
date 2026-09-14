package com.netec.pedidos.service;

import com.netec.pedidos.client.StockInsuficienteException;
import com.netec.pedidos.model.Pedido;
import com.netec.pedidos.port.CatalogoPort;
import com.netec.pedidos.port.ClienteInfo;
import com.netec.pedidos.port.ClientesPort;
import com.netec.pedidos.port.ProductoInfo;
import com.netec.pedidos.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * COMPARA ESTE METODO CON EL crear() DEL MONOLITO.
 *
 * Monolito (12 lineas, una anotacion):
 *     @Transactional
 *     public Pedido crear(...) {
 *         Cliente cliente   = clienteService.buscar(clienteId);
 *         Producto producto = productoService.buscar(productoId);
 *         productoService.descontarStock(productoId, cantidad);
 *         ... new Pedido() ...
 *         return pedidoRepository.save(pedido);
 *     }
 *
 * Aqui: la misma intencion, pero la atomicidad que daba @Transactional hay
 * que construirla a mano. Eso es una saga, y es el precio real de los
 * microservicios: no aparece en ningun diagrama de cajas.
 */
@Service
public class PedidoService {

    private final PedidoRepository pedidos;
    private final RegistroPedidos registro;
    private final CatalogoPort catalogo;
    private final ClientesPort clientes;

    public PedidoService(PedidoRepository pedidos,
                         RegistroPedidos registro,
                         CatalogoPort catalogo,
                         ClientesPort clientes) {
        this.pedidos = pedidos;
        this.registro = registro;
        this.catalogo = catalogo;
        this.clientes = clientes;
    }

    /**
     * Esta consulta NO llama a nadie: se responde entera desde la base de
     * datos local gracias a las copias historicas. Por eso sigue funcionando
     * aunque ms-productos y ms-clientes esten caidos.
     */
    public List<Pedido> listar() {
        return pedidos.findAll();
    }

    public Pedido crear(Long clienteId, Long productoId, int cantidad) {

        // --- lecturas: dos llamadas de red donde antes habia dos metodos ---
        ClienteInfo cliente = clientes.buscar(clienteId);
        ProductoInfo producto = catalogo.buscar(productoId);

        // La clave de idempotencia se genera ANTES de la primera escritura.
        // La red entrega el mismo mensaje dos veces con toda naturalidad; sin
        // esta clave, un reintento descontaria el stock por duplicado y nadie
        // se enteraria hasta el inventario.
        String opId = UUID.randomUUID().toString();

        // --- SAGA, paso 1: reservar ---
        // Va ANTES de guardar el pedido. Si guardaramos primero, podriamos
        // acabar con un pedido que nadie puede servir.
        catalogo.reservarStock(productoId, cantidad, opId);

        try {
            // --- paso 2: escritura local, dentro de su propia transaccion ---
            Pedido pedido = registro.guardar(cliente, producto, cantidad);

            // --- paso 3: confirmar ---
            catalogo.confirmarReserva(opId);
            return pedido;

        } catch (RuntimeException e) {
            // --- COMPENSACION ---
            // Esto es, linea por linea, lo que el monolito hacia gratis con
            // el rollback de una transaccion.
            catalogo.liberarReserva(opId);
            throw e;
        }
    }

    /** Solo para que la vista pueda distinguir el motivo del fallo. */
    public boolean esFaltaDeStock(RuntimeException e) {
        return e instanceof StockInsuficienteException;
    }
}
