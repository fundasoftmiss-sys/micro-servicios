package com.netec.pedidos.web;

import com.netec.pedidos.port.ConsultaCatalogoPort;
import com.netec.pedidos.port.ConsultaClientesPort;
import com.netec.pedidos.service.PedidoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final PedidoService pedidos;
    private final ConsultaCatalogoPort catalogo;
    private final ConsultaClientesPort clientes;

    public HomeController(PedidoService pedidos,
                          ConsultaCatalogoPort catalogo,
                          ConsultaClientesPort clientes) {
        this.pedidos = pedidos;
        this.catalogo = catalogo;
        this.clientes = clientes;
    }

    @GetMapping("/")
    public String home(Model model) {
        // Dato local: siempre disponible.
        model.addAttribute("totalPedidos", pedidos.listar().size());

        // Datos remotos: pueden fallar, y la pagina tiene que seguir sirviendo.
        // En el monolito esto era impensable, porque los tres contadores
        // salian de la misma consulta a la misma base de datos.
        try {
            model.addAttribute("totalProductos", catalogo.listarProductos().size());
            model.addAttribute("productosVivo", true);
        } catch (RuntimeException e) {
            model.addAttribute("productosVivo", false);
        }
        try {
            model.addAttribute("totalClientes", clientes.listarClientes().size());
            model.addAttribute("clientesVivo", true);
        } catch (RuntimeException e) {
            model.addAttribute("clientesVivo", false);
        }
        return "index";
    }
}
