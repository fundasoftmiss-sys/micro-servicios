package com.netec.pedidos.client;

import com.netec.pedidos.port.CatalogoPort;
import com.netec.pedidos.port.ConsultaCatalogoPort;
import com.netec.pedidos.port.ProductoInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * AQUI ESTA TODO EL CAMBIO DE LA MIGRACION.
 *
 * En el monolito esta clase no existia: PedidoService llamaba directamente a
 * ProductoService. El contrato (CatalogoPort) es identico; lo unico que
 * cambio es que detras hay una red.
 *
 * Y con la red aparecen tres cosas que antes no existian: codigos HTTP que
 * traducir, servicios que pueden estar caidos y timeouts que pueden saltar.
 */
@Component
public class CatalogoHttp implements CatalogoPort, ConsultaCatalogoPort {

    private static final String SERVICIO = "ms-productos";

    private final RestClient rest;

    public CatalogoHttp(@Qualifier("catalogoRestClient") RestClient rest) {
        this.rest = rest;
    }

    @Override
    public ProductoInfo buscar(Long productoId) {
        try {
            return rest.get()
                    .uri("/productos/{id}", productoId)
                    .retrieve()
                    .body(ProductoInfo.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RecursoNoEncontradoException("Producto no encontrado: " + productoId);
        } catch (ResourceAccessException e) {
            throw new ServicioNoDisponibleException(SERVICIO, e);
        }
    }

    @Override
    public List<ProductoInfo> listarProductos() {
        try {
            ProductoInfo[] productos = rest.get()
                    .uri("/productos")
                    .retrieve()
                    .body(ProductoInfo[].class);
            return productos == null ? List.of() : List.of(productos);
        } catch (ResourceAccessException e) {
            throw new ServicioNoDisponibleException(SERVICIO, e);
        }
    }

    @Override
    public void reservarStock(Long productoId, int cantidad, String opId) {
        try {
            rest.post()
                    .uri("/productos/reservas")
                    .body(Map.of("opId", opId, "productoId", productoId, "cantidad", cantidad))
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.Conflict e) {
            // 409 = regla de negocio incumplida. Traducimos el HTTP a una
            // excepcion de dominio para que el servicio no sepa de HTTP.
            throw new StockInsuficienteException(mensajeDe(e));
        } catch (HttpClientErrorException.NotFound e) {
            throw new RecursoNoEncontradoException("Producto no encontrado: " + productoId);
        } catch (ResourceAccessException e) {
            throw new ServicioNoDisponibleException(SERVICIO, e);
        }
    }

    @Override
    public void confirmarReserva(String opId) {
        try {
            rest.post().uri("/productos/reservas/{opId}/confirmar", opId)
                    .retrieve().toBodilessEntity();
        } catch (ResourceAccessException e) {
            throw new ServicioNoDisponibleException(SERVICIO, e);
        }
    }

    @Override
    public void liberarReserva(String opId) {
        try {
            rest.post().uri("/productos/reservas/{opId}/liberar", opId)
                    .retrieve().toBodilessEntity();
        } catch (RuntimeException e) {
            // La compensacion NO debe tapar el error original que la provoco.
            // En un sistema real esto va a una cola de reintentos y a una alerta:
            // una compensacion perdida deja stock reservado para siempre.
            System.err.println("!! No se pudo liberar la reserva " + opId + ": " + e.getMessage());
        }
    }

    private String mensajeDe(HttpClientErrorException e) {
        try {
            ErrorRemoto error = e.getResponseBodyAs(ErrorRemoto.class);
            if (error != null && error.mensaje() != null) {
                return error.mensaje();
            }
        } catch (RuntimeException ignored) {
            // el cuerpo no tenia el formato esperado
        }
        return "El catalogo rechazo la operacion";
    }
}
