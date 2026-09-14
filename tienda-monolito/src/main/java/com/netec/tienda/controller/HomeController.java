package com.netec.tienda.controller;

import com.netec.tienda.service.ClienteService;
import com.netec.tienda.service.PedidoService;
import com.netec.tienda.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ProductoService productoService;
    private final ClienteService clienteService;
    private final PedidoService pedidoService;

    public HomeController(ProductoService productoService, ClienteService clienteService, PedidoService pedidoService) {
        this.productoService = productoService;
        this.clienteService = clienteService;
        this.pedidoService = pedidoService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("totalProductos", productoService.listar().size());
        model.addAttribute("totalClientes", clienteService.listar().size());
        model.addAttribute("totalPedidos", pedidoService.listar().size());
        return "index";
    }
}
