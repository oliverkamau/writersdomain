package com.writers.modelapp.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class HttpWebClient {

   private final WebClient webClient;

    public HttpWebClient(WebClient webClient) {
        this.webClient = webClient;
    }


}
