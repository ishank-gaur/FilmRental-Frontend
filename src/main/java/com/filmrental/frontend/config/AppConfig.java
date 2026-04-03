package com.filmrental.frontend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Configuration
public class AppConfig {

    @Value("${backend.api.base-url}")
    private String backendBaseUrl;

    @Bean
    public RestTemplate restTemplate() {
        // Create cookie store with custom policy
        BasicCookieStore cookieStore = new BasicCookieStore();

        // Create HTTP client with cookie store and custom cookie policy
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();

        // Create request factory
        HttpComponentsClientHttpRequestFactory factory =
                new HttpComponentsClientHttpRequestFactory(httpClient);

        RestTemplate restTemplate = new RestTemplate(factory);

        // Add interceptor to log cookies for debugging
        restTemplate.setInterceptors(List.of(new ClientHttpRequestInterceptor() {
            @Override
            public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
                System.out.println("Request URI: " + request.getURI());
                System.out.println("Request Headers: " + request.getHeaders());
                if (request.getHeaders().get("Cookie") != null) {
                    System.out.println("Cookies being sent: " + request.getHeaders().get("Cookie"));
                } else {
                    System.out.println("No cookies being sent!");
                }
                return execution.execute(request, body);
            }
        }));

        return restTemplate;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(
                com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                false
        );
        return mapper;
    }

    @Bean
    public String backendBaseUrl() {
        return backendBaseUrl;
    }
}
