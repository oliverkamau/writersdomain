package com.writers.modelapp.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.time.Duration;

@Configuration
public class SSLConfig {

    @Value("${webclient.timeout}")
    private String timeout;

    @Bean
    public WebClient createWebClient() throws SSLException {
        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
        HttpClient httpClient = HttpClient.create().responseTimeout(Duration.ofMillis(Long.parseLong(timeout))).secure(t -> t.sslContext(sslContext));
        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }

}
