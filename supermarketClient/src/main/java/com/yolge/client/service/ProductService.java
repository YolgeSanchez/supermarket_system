package com.yolge.client.service;

import com.yolge.client.core.RestClient;
import com.yolge.client.dto.PageResponse;
import com.yolge.client.dto.product.ProductRequest;
import com.yolge.client.dto.product.ProductResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ProductService {
    
    private static ProductService instance;
    private final RestClient restClient;
    
    private ProductService() {
        this.restClient = RestClient.getInstance();
    }
    
    public static synchronized ProductService getInstance() {
        if (instance == null) {
            instance = new ProductService();
        }
        return instance;
    }

    // En ProductService.java
    public void createProduct(ProductRequest request) throws Exception {
        // Asumimos que el endpoint es POST /products
        restClient.post("/products", request, ProductResponse.class);
    }

    // Este método está bien porque no recibe Strings libres
    public PageResponse<ProductResponse> getProducts(int page, int size) {
        String endpoint = String.format("/products?page=%d&size=%d", page, size);
        return restClient.getPage(endpoint, ProductResponse.class);
    }

    public PageResponse<ProductResponse> searchByName(int page, int size, String name) {
        // SOLUCIÓN: Codificar el parámetro para que "leche s" sea "leche%20s"
        String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8);

        String endpoint = String.format("/products?page=%d&size=%d&name=%s",
                page, size, encodedName);
        return restClient.getPage(endpoint, ProductResponse.class);
    }

    public PageResponse<ProductResponse> searchByBrand(int page, int size, String brand) {
        String encodedBrand = URLEncoder.encode(brand, StandardCharsets.UTF_8);

        String endpoint = String.format("/products?page=%d&size=%d&brand=%s",
                page, size, encodedBrand);
        return restClient.getPage(endpoint, ProductResponse.class);
    }

    public PageResponse<ProductResponse> searchByCategory(int page, int size, String categoryName) {
        String encodedCat = URLEncoder.encode(categoryName, StandardCharsets.UTF_8);

        String endpoint = String.format("/products?page=%d&size=%d&categoryName=%s",
                page, size, encodedCat);
        return restClient.getPage(endpoint, ProductResponse.class);
    }

    public ProductResponse getProductById(Long id) {
        return restClient.get("/products/" + id, ProductResponse.class);
    }

    // 2. Actualizar (PUT)
    public void updateProduct(Long id, ProductRequest request) {
        restClient.put("/products/" + id, request, ProductResponse.class);
    }

    // 3. Eliminar (DELETE)
    public void deleteProduct(Long id) {
        // Usamos DeleteProductResponse.class porque tu back devuelve un objeto JSON, no vacío
        restClient.delete("/products/" + id, com.yolge.client.dto.product.DeleteProductResponse.class);
    }
}