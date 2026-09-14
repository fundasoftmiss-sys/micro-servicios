package com.netec.productos.web;

import com.netec.productos.model.Producto;
import com.netec.productos.model.Reserva;
import com.netec.productos.service.ProductoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * La API publica de ms-productos. Este es ahora el UNICO modo de llegar a la
 * tabla PRODUCTO: nadie mas la consulta directamente.
 */
@RestController
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Producto> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Producto buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public Producto crear(@RequestBody Producto producto) {
        return service.guardar(producto);
    }

    // ---------- saga ----------

    @PostMapping("/reservas")
    public Reserva reservar(@RequestBody ReservaRequest req) {
        return service.reservar(req.opId(), req.productoId(), req.cantidad());
    }

    @PostMapping("/reservas/{opId}/confirmar")
    public Reserva confirmar(@PathVariable String opId) {
        return service.confirmar(opId);
    }

    @PostMapping("/reservas/{opId}/liberar")
    public Reserva liberar(@PathVariable String opId) {
        return service.liberar(opId);
    }

    /** Util en clase: ver el rastro de la saga. */
    @GetMapping("/reservas")
    public List<Reserva> reservas() {
        return service.listarReservas();
    }
}
