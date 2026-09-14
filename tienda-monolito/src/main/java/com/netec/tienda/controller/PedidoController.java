package com.netec.tienda.controller;

import com.netec.tienda.service.ClienteService;
import com.netec.tienda.service.PedidoService;
import com.netec.tienda.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final ClienteService clienteService;
    private final ProductoService productoService;

    public PedidoController(PedidoService pedidoService, ClienteService clienteService, ProductoService productoService) {
        this.pedidoService = pedidoService;
        this.clienteService = clienteService;
        this.productoService = productoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pedidos", pedidoService.listar());
        model.addAttribute("clientes", clienteService.listar());
        model.addAttribute("productos", productoService.listar());
        return "pedidos";
    }

    @PostMapping
    public String crear(@RequestParam Long clienteId,
                        @RequestParam Long productoId,
                        @RequestParam int cantidad,
                        RedirectAttributes ra) {
        try {
            pedidoService.crear(clienteId, productoId, cantidad);
            ra.addFlashAttribute("ok", "Pedido creado correctamente");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/pedidos";
    }
}
