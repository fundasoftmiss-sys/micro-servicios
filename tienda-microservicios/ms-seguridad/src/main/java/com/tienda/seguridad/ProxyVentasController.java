package com.tienda.seguridad;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ventas")
public class ProxyVentasController {

    private final ProxyHttpService proxy;
    private final String url;

    public ProxyVentasController(ProxyHttpService proxy,
                                 @Value("${servicios.ventas.url}") String url) {
        this.proxy = proxy;
        this.url = url;
    }

    @GetMapping
    public ResponseEntity<byte[]> listar() {
        return proxy.get(url + "/api/ventas");
    }

    @PostMapping
    public ResponseEntity<byte[]> crear(@RequestBody byte[] body) {
        return proxy.post(url + "/api/ventas", body, MediaType.APPLICATION_JSON);
    }
}