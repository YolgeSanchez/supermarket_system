package com.yolge.client.core;

import com.fasterxml.jackson.databind.DeserializationFeature;
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

    private RestClient() {
        // 1. Cliente HTTP optimizado
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2) // Usa HTTP/2 si es posible (más rápido)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        // 2. Jackson Configurado
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule()); // Fechas Java 8
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // Evita errores si el backend agrega campos nuevos

        // 3. Carga segura de .env
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        this.baseUrl = dotenv.get("API_URL", "http://localhost:8080/api/v0.0"); // Valor por defecto por si acaso
    }

    public static synchronized RestClient getInstance() {
        if (instance == null) {
            instance = new RestClient();
        }
        return instance;
    }

    // --- AUTENTICACIÓN ---

    public void login(String username, String password) throws Exception {
        AuthRequest loginRequest = new AuthRequest(username, password); // Asegúrate de tener este DTO creado
        
        // Reutilizamos el método post interno
        AuthResponse response = post("/auth/login", loginRequest, AuthResponse.class);
        
        this.authToken = response.getToken();
    }
    
    public void logout() {
        this.authToken = null;
    }

    public boolean isLoggedIn() {
        return authToken != null;
    }

    // --- MÉTODOS HTTP GENÉRICOS ---

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
    
    // Método para PUT sin body (ej: actualizar cantidad en URL)
    public <T> T put(String endpoint, Class<T> responseType) {
        HttpRequest request = buildRequest(endpoint)
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
        return execute(request, responseType);
    }

    public <T> T patch(String endpoint, Class<T> responseType) {
         // Java 11 HttpClient no tiene .PATCH() nativo fácil, usamos method()
        HttpRequest request = buildRequest(endpoint)
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();
        return execute(request, responseType);
    }

    public <T> T delete(String endpoint, Class<T> responseType) {
        HttpRequest request = buildRequest(endpoint).DELETE().build();
        return execute(request, responseType);
    }

    // --- INTERNOS (La Maquinaria) ---

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
            throw e; // Re-lanzar nuestra excepción limpia
        } catch (Exception e) {
            // Error de red o conexión
            throw new ApiException("Error de comunicación con el servidor: " + e.getMessage(), 503);
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseType) {
        // 1. Manejo de Errores (400 - 599)
        if (response.statusCode() >= 400) {
            String finalMessage = "Error del servidor (" + response.statusCode() + ")";

            try {
                // INTENTO 1: ¿Es un error de Validación de Campos (@Valid)?
                // Jackson lanzará excepción si el JSON no coincide con la estructura
                ValidationErrorDto validationError = mapper.readValue(response.body(), ValidationErrorDto.class);

                if (validationError.getErrors() != null && !validationError.getErrors().isEmpty()) {
                    throw new ApiValidationException(validationError.getErrors());
                }
            } catch (ApiValidationException ave) {
                throw ave; // Si ya creamos la excepcion arriba, la dejamos pasar
            } catch (Exception ignored) {
                // No era un ValidationErrorDto, seguimos intentando...
            }

            try {
                // INTENTO 2: ¿Es un ErrorDto estándar (Logic/Not Found)?
                ErrorDto errorDto = mapper.readValue(response.body(), ErrorDto.class);
                if (errorDto.getMessage() != null) {
                    finalMessage = errorDto.getMessage();
                }
            } catch (Exception ignored) {
                // INTENTO 3: Fallback al texto crudo si todo falla
                if (response.body() != null && !response.body().isBlank()) {
                    finalMessage = response.body();
                }
            }

            // Lanzamos la excepción final con el mejor mensaje que pudimos encontrar
            throw new ApiException(finalMessage, response.statusCode());
        }

        // 2. Manejo de Éxito (Igual que antes)
        if (responseType == Void.class || response.body().isBlank()) {
            return null;
        }

        try {
            return mapper.readValue(response.body(), responseType);
        } catch (Exception e) {
            throw new ApiException("Error al procesar respuesta: " + e.getMessage(), 500);
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