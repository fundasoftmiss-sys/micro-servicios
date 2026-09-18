package com.netec.ventas.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class PedidosClientConfig {

    @Bean
    RestClient pedidosRestClient(RestClient.Builder builder,
                                 @Value("${servicios.pedidos.url}") String url,
                                 @Value("${servicios.timeout.conexion-ms}") int conexionMs,
                                 @Value("${servicios.timeout.lectura-ms}") int lecturaMs) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(conexionMs);
        factory.setReadTimeout(lecturaMs);
        return builder.baseUrl(url).requestFactory(factory).build();
    }
}
