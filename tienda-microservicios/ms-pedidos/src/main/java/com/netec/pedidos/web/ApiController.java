package com.netec.pedidos.web;

import com.netec.pedidos.model.Pedido;
import com.netec.pedidos.service.PedidoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Del ApiController del monolito solo queda esto.
 *
 * /api/productos se fue a ms-productos (puerto 8081) y /api/clientes a
 * ms-clientes (8082). Cada servicio publica lo suyo; componer las tres
 * respuestas en una sola es trabajo de un API Gateway.
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    private final PedidoService pedidoService;

    public ApiController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/pedidos")
    public List<Pedido> pedidos() {
        return pedidoService.listar();
    }
}
