package com.netec.tienda.controller;

import com.netec.tienda.model.Cliente;
import com.netec.tienda.model.Pedido;
import com.netec.tienda.model.Producto;
import com.netec.tienda.service.ClienteService;
import com.netec.tienda.service.PedidoService;
import com.netec.tienda.service.ProductoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * API REST del monolito. Fijense: /api/productos, /api/clientes y /api/pedidos
 * viven en la MISMA aplicacion y el MISMO puerto.
 * En microservicios cada una seria una aplicacion independiente.
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    private final ProductoService productoService;
    private final ClienteService clienteService;
    private final PedidoService pedidoService;

    public ApiController(ProductoService productoService, ClienteService clienteService, PedidoService pedidoService) {
        this.productoService = productoService;
        this.clienteService = clienteService;
        this.pedidoService = pedidoService;
    }

    @GetMapping("/productos")
    public List<Producto> productos() { return productoService.listar(); }


    @GetMapping("/clientes")
    public List<Cliente> clientes() { return clienteService.listar(); }

    @GetMapping("/pedidos")
    public List<Pedido> pedidos() { return pedidoService.listar(); }
}
