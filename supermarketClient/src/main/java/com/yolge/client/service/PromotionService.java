package com.yolge.client.service;

import com.yolge.client.core.RestClient;
import com.yolge.client.dto.PageResponse;
import com.yolge.client.dto.promotion.DeletePromotionResponse;
import com.yolge.client.dto.promotion.PromotionRequest;
import com.yolge.client.dto.promotion.PromotionResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class PromotionService {

    private static PromotionService instance;
    private final RestClient restClient;

    private PromotionService() {
        this.restClient = RestClient.getInstance();
    }

    public static synchronized PromotionService getInstance() {
        if (instance == null) {
            instance = new PromotionService();
        }
        return instance;
    }

    public PageResponse<PromotionResponse> getAll(int page, int size) {
        String endpoint = String.format("/promotions?page=%d&size=%d", page, size);
        return restClient.getPage(endpoint, PromotionResponse.class);
    }

    public PageResponse<PromotionResponse> searchByName(int page, int size, String name) {
        String encoded = URLEncoder.encode(name, StandardCharsets.UTF_8);
        String endpoint = String.format("/promotions?page=%d&size=%d&name=%s", page, size, encoded);
        return restClient.getPage(endpoint, PromotionResponse.class);
    }

    public PromotionResponse getById(Long id) {
        return restClient.get("/promotions/" + id, PromotionResponse.class);
    }

    public void createPromotion(PromotionRequest request) {
        restClient.post("/promotions", request, PromotionResponse.class);
    }

    public void deleteById(Long id) {
        restClient.delete("/promotions/" + id, DeletePromotionResponse.class);
    }
}