package com.yolge.client.service;

import com.yolge.client.core.RestClient;
import com.yolge.client.dto.PageResponse;
import com.yolge.client.dto.category.CategoryResponse;

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

    public PageResponse<CategoryResponse> getAllCategories() {
        String endpoint = "/categories?page=0&size=100";
        return restClient.getPage(endpoint, CategoryResponse.class);
    }
}