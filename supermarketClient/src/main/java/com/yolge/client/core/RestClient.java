package com.yolge.client.core;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.yolge.client.dto.auth.AuthRequest;   // (Debes crear este DTO simple)
import com.yolge.client.dto.auth.AuthResponse;  // (Debes crear este DTO simple)
import com.yolge.client.dto.error.ErrorDto;
import com.yolge.client.dto.error.ValidationErrorDto;
import com.yolge.client.exceptions.ApiException;
import com.yolge.client.exceptions.ApiValidationException;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class RestClient {

    private static RestClient instance;
    private final HttpClient client;
    private final ObjectMapper mapper;
    private final String baseUrl;
    private String authToken;
    private String username;
    private String currentRole;

    private RestClient() {
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        this.baseUrl = dotenv.get("API_URL", "http://localhost:8080/api/v0.0");
    }

    public static synchronized RestClient getInstance() {
        if (instance == null) {
            instance = new RestClient();
        }
        return instance;
    }

    public void login(String username, String password) throws Exception {
        AuthRequest loginRequest = new AuthRequest(username, password);
        
        AuthResponse response = post("/auth/login", loginRequest, AuthResponse.class);

        this.authToken = response.getToken();
        this.username = username;
        this.currentRole = extractRoleFromToken(this.authToken);
    }
    
    public void logout() {
        this.authToken = null;
        this.username = null;
        this.currentRole = null;
    }

    public boolean isLoggedIn() {
        return authToken != null;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return currentRole;
    }

    public boolean isAdmin() {
        return "ROLE_ADMIN".equals(currentRole) || "ADMIN".equals(currentRole);
    }

    public boolean isCashier() {
        return "ROLE_CASHIER".equals(currentRole) || "CASHIER".equals(currentRole);
    }

    public boolean isInventory() {
        return "ROLE_INVENTORY".equals(currentRole) || "INVENTORY".equals(currentRole);
    }

    public <T> T get(String endpoint, Class<T> responseType) {
        HttpRequest request = buildRequest(endpoint).GET().build();
        return execute(request, responseType);
    }

    public <T> T post(String endpoint, Object body, Class<T> responseType) {
        HttpRequest request = buildRequest(endpoint)
                .POST(HttpRequest.BodyPublishers.ofString(toJson(body), StandardCharsets.UTF_8))
                .build();
        return execute(request, responseType);
    }

    public <T> T put(String endpoint, Object body, Class<T> responseType) {
        HttpRequest request = buildRequest(endpoint)
                .PUT(HttpRequest.BodyPublishers.ofString(toJson(body), StandardCharsets.UTF_8))
                .build();
        return execute(request, responseType);
    }
    
    public <T> T put(String endpoint, Class<T> responseType) {
        HttpRequest request = buildRequest(endpoint)
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
        return execute(request, responseType);
    }

    public <T> T patch(String endpoint, Class<T> responseType) {
        HttpRequest request = buildRequest(endpoint)
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();
        return execute(request, responseType);
    }

    public <T> T delete(String endpoint, Class<T> responseType) {
        HttpRequest request = buildRequest(endpoint).DELETE().build();
        return execute(request, responseType);
    }

    private HttpRequest.Builder buildRequest(String endpoint) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + endpoint))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");

        if (authToken != null) {
            builder.header("Authorization", "Bearer " + authToken);
        }

        return builder;
    }

    private <T> T execute(HttpRequest request, Class<T> responseType) {
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return handleResponse(response, responseType);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Error de comunicación con el servidor: " + e.getMessage(), 503);
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseType) {
        if (response.statusCode() >= 400) {
            String finalMessage = "Error del servidor (" + response.statusCode() + ")";

            try {
                ValidationErrorDto validationError = mapper.readValue(response.body(), ValidationErrorDto.class);

                if (validationError.getErrors() != null && !validationError.getErrors().isEmpty()) {
                    throw new ApiValidationException(validationError.getErrors());
                }
            } catch (ApiValidationException ave) {
                throw ave;
            } catch (Exception ignored) {
            }

            try {
                ErrorDto errorDto = mapper.readValue(response.body(), ErrorDto.class);
                if (errorDto.getMessage() != null) {
                    finalMessage = errorDto.getMessage();
                }
            } catch (Exception ignored) {
                if (response.body() != null && !response.body().isBlank()) {
                    finalMessage = response.body();
                }
            }

            throw new ApiException(finalMessage, response.statusCode());
        }

        if (responseType == Void.class || response.body().isBlank()) {
            return null;
        }

        try {
            return mapper.readValue(response.body(), responseType);
        } catch (Exception e) {
            throw new ApiException("Error al procesar respuesta: " + e.getMessage(), 500);
        }
    }

    private String extractRoleFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) return null;

            String payloadJson = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));

            JsonNode node = mapper.readTree(payloadJson);

            if (node.has("role")) {
                System.out.println(node.get("role").asText());
                return node.get("role").asText();
            }
            return null;

        } catch (Exception e) {
            System.err.println("Error al decodificar token: " + e.getMessage());
            return null;
        }
    }

    private String toJson(Object data) {
        try {
            return mapper.writeValueAsString(data);
        } catch (Exception e) {
            throw new ApiException("Error al crear JSON: " + e.getMessage(), 400);
        }
    }
}