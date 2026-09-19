package com.tienda.seguridad;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/productos")
public class ProxyProductosController {

    private final ProxyHttpService proxy;
    private final String url;

    public ProxyProductosController(ProxyHttpService proxy,
                                    @Value("${servicios.productos.url}") String url) {
        this.proxy = proxy;
        this.url = url;
    }

    @GetMapping
    public ResponseEntity<byte[]> listar() {
        return proxy.get(url + "/productos");
    }

    @PostMapping
    public ResponseEntity<byte[]> crear(@RequestBody byte[] body) {
        return proxy.post(url + "/productos", body, MediaType.APPLICATION_JSON);
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> buscar(@PathVariable Long id) {
        return proxy.get(url + "/productos/" + id);
    }

    @GetMapping("/reservas")
    public ResponseEntity<byte[]> reservas() {
        return proxy.get(url + "/productos/reservas");
    }

    @PostMapping("/reservas")
    public ResponseEntity<byte[]> reservar(@RequestBody byte[] body) {
        return proxy.post(url + "/productos/reservas", body, MediaType.APPLICATION_JSON);
    }

    @PostMapping("/reservas/{opId}/confirmar")
    public ResponseEntity<byte[]> confirmar(@PathVariable String opId) {
        return proxy.post(url + "/productos/reservas/" + opId + "/confirmar", null, null);
    }

    @PostMapping("/reservas/{opId}/liberar")
    public ResponseEntity<byte[]> liberar(@PathVariable String opId) {
        return proxy.post(url + "/productos/reservas/" + opId + "/liberar", null, null);
    }
}