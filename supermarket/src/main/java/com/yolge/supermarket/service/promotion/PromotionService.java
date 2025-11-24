package com.yolge.supermarket.service.promotion;

import com.yolge.supermarket.dto.PageResponse;
import com.yolge.supermarket.dto.promotion.DeletePromotionResponse;
import com.yolge.supermarket.dto.promotion.PromotionRequest;
import com.yolge.supermarket.dto.promotion.PromotionResponse;

public interface PromotionService {
    PromotionResponse createPromotion(PromotionRequest request);
    DeletePromotionResponse deleteById(Long id);
    PromotionResponse getById(Long id);
    PageResponse<PromotionResponse> getAll(int page, int size);
    PageResponse<PromotionResponse> searchByName(int page, int size, String name);
}
