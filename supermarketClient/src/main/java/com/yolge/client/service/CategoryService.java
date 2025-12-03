package com.yolge.client.service;

import com.yolge.client.core.RestClient;
import com.yolge.client.dto.PageResponse;
import com.yolge.client.dto.category.CategoryRequest;
import com.yolge.client.dto.category.CategoryResponse;
import com.yolge.client.dto.category.DeleteCategoryResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CategoryService {
    
    private static CategoryService instance;
    private final RestClient restClient;
    
    private CategoryService() {
        this.restClient = RestClient.getInstance();
    }
    
    public static synchronized CategoryService getInstance() {
        if (instance == null) {
            instance = new CategoryService();
        }
        return instance;
    }

    public PageResponse<CategoryResponse> getAll(int page, int size) {
        String endpoint = String.format("/categories?page=%d&size=%d", page, size);
        return restClient.getPage(endpoint, CategoryResponse.class);
    }

    public PageResponse<CategoryResponse> searchByName(int page, int size, String name) {
        String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8);
        String endpoint = String.format("/categories?page=%d&size=%d&name=%s", page, size, encodedName);
        return restClient.getPage(endpoint, CategoryResponse.class);
    }

    public CategoryResponse getById(Long id) {
        return restClient.get("/categories/" + id, CategoryResponse.class);
    }

    public void createCategory(CategoryRequest request) {
        restClient.post("/categories", request, CategoryResponse.class);
    }

    public void updateCategory(Long id, CategoryRequest request) {
        restClient.put("/categories/" + id, request, CategoryResponse.class);
    }

    public void deleteCategory(Long id) {
        restClient.delete("/categories/" + id, DeleteCategoryResponse.class);
    }

    public PageResponse<CategoryResponse> getAllCategories() {
        return getAll(0, 100);
    }
}