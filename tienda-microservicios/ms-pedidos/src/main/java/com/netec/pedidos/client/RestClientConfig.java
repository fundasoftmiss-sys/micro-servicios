package com.netec.pedidos.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * Configuracion que en el monolito no tenia sentido.
 *
 * Un metodo Java se ejecuta o lanza una excepcion, y punto. Una llamada de
 * red puede quedarse esperando para siempre, y mientras tanto ocupa un hilo
 * de este servicio. Sin timeout, un ms-productos lento tumba a ms-pedidos:
 * se agota el pool de hilos y cae el servicio que SI funcionaba.
 *
 * Es el fallo numero uno al extraer el primer servicio.
 */
@Configuration
public class RestClientConfig {

    private SimpleClientHttpRequestFactory fabrica(int conexionMs, int lecturaMs) {
        SimpleClientHttpRequestFactory f = new SimpleClientHttpRequestFactory();
        f.setConnectTimeout(conexionMs);
        f.setReadTimeout(lecturaMs);
        return f;
    }

    @Bean
    public RestClient catalogoRestClient(
            RestClient.Builder builder,
            @Value("${servicios.productos.url}") String url,
            @Value("${servicios.timeout.conexion-ms}") int conexionMs,
            @Value("${servicios.timeout.lectura-ms}") int lecturaMs) {
        return builder.baseUrl(url).requestFactory(fabrica(conexionMs, lecturaMs)).build();
    }

    @Bean
    public RestClient clientesRestClient(
            RestClient.Builder builder,
            @Value("${servicios.clientes.url}") String url,
            @Value("${servicios.timeout.conexion-ms}") int conexionMs,
            @Value("${servicios.timeout.lectura-ms}") int lecturaMs) {
        return builder.baseUrl(url).requestFactory(fabrica(conexionMs, lecturaMs)).build();
    }
}
