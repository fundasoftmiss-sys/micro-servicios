package com.netec.tienda.controller;

import com.netec.tienda.model.Producto;
import com.netec.tienda.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", productoService.listar());
        return "productos";
    }

    @PostMapping
    public String crear(@RequestParam String nombre,
                        @RequestParam double precio,
                        @RequestParam int stock) {
        productoService.guardar(new Producto(nombre, precio, stock));
        return "redirect:/productos";
    }
}
