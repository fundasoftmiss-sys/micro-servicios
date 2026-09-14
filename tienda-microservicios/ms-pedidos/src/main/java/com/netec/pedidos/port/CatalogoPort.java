package com.netec.pedidos.port;

/**
 * CONTRATO DE NEGOCIO: lo minimo que Pedidos necesita del catalogo.
 *
 * Fijate en lo que NO esta: listar() ni guardar(). Crear productos es asunto
 * del catalogo, y si Pedidos pudiera llamarlo, la frontera no existiria.
 *
 * Este interfaz nacio en la fase 1 de la migracion, cuando todo seguia en el
 * mismo proceso. Gracias a eso, PedidoService no cambio ni una linea al
 * sustituir la implementacion local por la implementacion HTTP.
 */
public interface CatalogoPort {

    ProductoInfo buscar(Long productoId);

    /** Paso 1 de la saga: descuenta stock de forma idempotente segun opId. */
    void reservarStock(Long productoId, int cantidad, String opId);

    /** Paso 2 de la saga: el pedido se guardo bien. */
    void confirmarReserva(String opId);

    /** Compensacion: devuelve el stock. Sustituye al rollback del monolito. */
    void liberarReserva(String opId);
}
