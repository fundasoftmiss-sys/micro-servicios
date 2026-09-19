package com.tienda.seguridad;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pedidos")
public class ProxyPedidosController {

    private final ProxyHttpService proxy;
    private final String url;

    public ProxyPedidosController(ProxyHttpService proxy,
                                  @Value("${servicios.pedidos.url}") String url) {
        this.proxy = proxy;
        this.url = url;
    }

    @GetMapping
    public ResponseEntity<byte[]> pagina() {
        return proxy.get(url + "/pedidos");
    }

    @PostMapping
    public ResponseEntity<byte[]> crearDesdeFormulario(
            @RequestBody(required = false) byte[] body,
            @RequestHeader(value = "Content-Type", required = false) MediaType contentType) {
        return proxy.post(url + "/pedidos", body, contentType);
    }

    @GetMapping("/api/pedidos")
    public ResponseEntity<byte[]> listar() {
        return proxy.get(url + "/api/pedidos");
    }

    @PostMapping("/api/pedidos")
    public ResponseEntity<byte[]> crear(@RequestBody byte[] body) {
        return proxy.post(url + "/api/pedidos", body, MediaType.APPLICATION_JSON);
    }
}