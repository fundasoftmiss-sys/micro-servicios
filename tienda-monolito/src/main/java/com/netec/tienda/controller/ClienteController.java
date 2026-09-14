package com.netec.tienda.controller;

import com.netec.tienda.model.Cliente;
import com.netec.tienda.service.ClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", clienteService.listar());
        return "clientes";
    }

    @PostMapping
    public String crear(@RequestParam String nombre, @RequestParam String email) {
        clienteService.guardar(new Cliente(nombre, email));
        return "redirect:/clientes";
    }
}
