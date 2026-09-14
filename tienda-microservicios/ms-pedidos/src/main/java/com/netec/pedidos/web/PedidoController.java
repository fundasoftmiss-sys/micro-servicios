package com.netec.pedidos.web;

import com.netec.pedidos.client.ServicioNoDisponibleException;
import com.netec.pedidos.port.ConsultaCatalogoPort;
import com.netec.pedidos.port.ConsultaClientesPort;
import com.netec.pedidos.service.PedidoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * EL MISMO CONTROLADOR DEL MONOLITO.
 *
 * Mismas rutas, mismo formulario, mismo try/catch que pinta el error en la
 * vista. Lo unico que cambio: las listas del formulario ahora llegan por red
 * y por tanto pueden no llegar.
 */
@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final ConsultaCatalogoPort catalogo;
    private final ConsultaClientesPort clientes;

    public PedidoController(PedidoService pedidoService,
                            ConsultaCatalogoPort catalogo,
                            ConsultaClientesPort clientes) {
        this.pedidoService = pedidoService;
        this.catalogo = catalogo;
        this.clientes = clientes;
    }

    @GetMapping
    public String listar(Model model) {
        // La tabla de pedidos sale entera de la base de datos local.
        model.addAttribute("pedidos", pedidoService.listar());

        // El formulario necesita los otros dos servicios. Si alguno no esta,
        // se degrada: se sigue viendo el historico, pero no se puede pedir.
        String aviso = null;
        try {
            model.addAttribute("productos", catalogo.listarProductos());
        } catch (ServicioNoDisponibleException e) {
            model.addAttribute("productos", List.of());
            aviso = e.getMessage();
        }
        try {
            model.addAttribute("clientes", clientes.listarClientes());
        } catch (ServicioNoDisponibleException e) {
            model.addAttribute("clientes", List.of());
            aviso = e.getMessage();
        }
        model.addAttribute("aviso", aviso);
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
