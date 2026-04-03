package com.filmrental.frontend.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class ApiService {

    private final RestTemplate restTemplate;
    private final String backendBaseUrl;
    private String storedCookie; // Store the cookie manually

    public ApiService(RestTemplate restTemplate,
                      @Qualifier("backendBaseUrl") String backendBaseUrl) {
        this.restTemplate = restTemplate;
        this.backendBaseUrl = backendBaseUrl;
    }

    // ─────────────────────────────────────────
    //  Auth
    // ─────────────────────────────────────────
    public String login(String username, String password) {
        String url = backendBaseUrl + "/api/v1/auth/login";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of(
                "username", username,
                "password", password
        );

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            String responseBody = response.getBody();

            System.out.println("Login response status: " + response.getStatusCode());
            System.out.println("Login response headers: " + response.getHeaders());
            System.out.println("Login response body: " + responseBody);

            // Extract and store the cookie
            String setCookieHeader = response.getHeaders().getFirst("Set-Cookie");
            if (setCookieHeader != null && setCookieHeader.contains("token=")) {
                // Extract just the token=value part (ignore other attributes like Path, Secure, etc.)
                int tokenIndex = setCookieHeader.indexOf("token=");
                int semicolonIndex = setCookieHeader.indexOf(";", tokenIndex);
                if (semicolonIndex != -1) {
                    storedCookie = setCookieHeader.substring(tokenIndex, semicolonIndex);
                } else {
                    storedCookie = setCookieHeader.substring(tokenIndex);
                }
                System.out.println("Stored cookie: " + storedCookie);
            }

            // Parse JSON response to extract token (though we might not need it for cookie-based auth)
            if (responseBody != null && responseBody.contains("\"token\"")) {
                // Simple JSON parsing to extract token value
                int tokenIndex = responseBody.indexOf("\"token\"");
                if (tokenIndex != -1) {
                    int colonIndex = responseBody.indexOf(":", tokenIndex);
                    if (colonIndex != -1) {
                        int startQuote = responseBody.indexOf("\"", colonIndex);
                        if (startQuote != -1) {
                            int endQuote = responseBody.indexOf("\"", startQuote + 1);
                            if (endQuote != -1) {
                                return responseBody.substring(startQuote + 1, endQuote);
                            }
                        }
                    }
                }
            }

            return "success"; // Return success for cookie-based auth
        } catch (HttpClientErrorException e) {
            System.out.println("Login error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return null;
        }
    }

    // ─────────────────────────────────────────
    //  Generic authenticated GET
    // ─────────────────────────────────────────
    private HttpHeaders cookieHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // Add the stored cookie if available
        if (storedCookie != null && !storedCookie.isEmpty()) {
            headers.set("Cookie", storedCookie);
            System.out.println("Adding cookie to request: " + storedCookie);
        } else {
            System.out.println("No stored cookie available!");
        }

        return headers;
    }

    public <T> T getForObject(String path, String token, Class<T> responseType) {
        String url = backendBaseUrl + path;
        HttpEntity<Void> entity = new HttpEntity<>(cookieHeaders());
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
        return response.getBody();
    }

    public <T> List<T> getForList(String path, String token, ParameterizedTypeReference<List<T>> typeRef) {
        String url = backendBaseUrl + path;
        HttpEntity<Void> entity = new HttpEntity<>(cookieHeaders());

        try {
            ResponseEntity<List<T>> response = restTemplate.exchange(url, HttpMethod.GET, entity, typeRef);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            System.out.println("API error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            System.out.println("API exception: " + e.getMessage());
            throw e;
        }
    }

    // ─────────────────────────────────────────
    //  API endpoints
    // ─────────────────────────────────────────

    public List<Map<String, Object>> getCustomerRentals(String token, int customerId) {
        return getForList(
                "/api/v1/customers/" + customerId + "/rentals",
                token,
                new ParameterizedTypeReference<>() {}
        );
    }

    public List<Object[]> getFilmActors(String token, int filmId) {
        return getForList(
                "/api/v1/films/" + filmId + "/actors",
                token,
                new ParameterizedTypeReference<List<Object[]>>() {}
        );
    }

    public List<Map<String, Object>> getPaymentDetails(String token) {
        return getForList(
                "/api/v1/payments/details",
                token,
                new ParameterizedTypeReference<>() {}
        );
    }

    public List<Map<String, Object>> getRentalStaff(String token) {
        return getForList(
                "/api/v1/rentals/staff",
                token,
                new ParameterizedTypeReference<>() {}
        );
    }

    public List<Map<String, Object>> getRentalFilms(String token) {
        return getForList(
                "/api/v1/rentals/films",
                token,
                new ParameterizedTypeReference<>() {}
        );
    }
}
