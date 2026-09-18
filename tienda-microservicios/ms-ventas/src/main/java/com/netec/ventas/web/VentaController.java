package com.netec.ventas.web;

import com.netec.ventas.model.Venta;
import com.netec.ventas.service.VentaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService service;

    public VentaController(VentaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Venta> listar() {
        return service.listar();
    }

    @PostMapping
    public Venta crear(@RequestBody CrearVentaRequest request) {
        return service.crear(request.clienteId(), request.productoId(), request.cantidad());
    }

    public record CrearVentaRequest(Long clienteId, Long productoId, int cantidad) {}
}
