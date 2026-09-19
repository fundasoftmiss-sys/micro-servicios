package com.tienda.seguridad;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ControladorSeguridad {

    private final ProxyHttpService proxy;
    private final String url;

    public ControladorSeguridad(ProxyHttpService proxy,
                                @Value("${servicios.pedidos.url}") String url) {
        this.proxy = proxy;
        this.url = url;
    }

    @GetMapping("/")
    public ResponseEntity<byte[]> pedidos() {
        return proxy.get(url + "/");
    }
}