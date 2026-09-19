package com.tienda.seguridad;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class ProxyHttpService {

    private final RestClient.Builder restClientBuilder;

    public ProxyHttpService(RestClient.Builder restClientBuilder) {
        this.restClientBuilder = restClientBuilder;
    }

    public ResponseEntity<byte[]> get(String url) {
        try {
            return restClientBuilder.build()
                    .get()
                    .uri(url)
                    .exchange((request, response) -> respuesta(response));
        } catch (RestClientException e) {
            return servicioNoDisponible(url);
        }
    }

    public ResponseEntity<byte[]> post(String url, byte[] body, MediaType contentType) {
        try {
            RestClient.RequestBodySpec request = restClientBuilder.build()
                    .post()
                    .uri(url);
            if (contentType != null) {
                request.contentType(contentType);
            }
            return request.body(body == null ? new byte[0] : body)
                    .exchange((requestMessage, response) -> respuesta(response));
        } catch (RestClientException e) {
            return servicioNoDisponible(url);
        }
    }

    private ResponseEntity<byte[]> respuesta(org.springframework.http.client.ClientHttpResponse response)
            throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(response.getHeaders());
        return ResponseEntity.status(response.getStatusCode())
                .headers(headers)
                .body(response.getBody().readAllBytes());
    }

    private ResponseEntity<byte[]> servicioNoDisponible(String url) {
        String mensaje = "Servicio no disponible: " + url;
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.TEXT_PLAIN)
                .body(mensaje.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}