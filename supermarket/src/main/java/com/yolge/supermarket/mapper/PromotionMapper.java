package com.yolge.supermarket.mapper;

import com.yolge.supermarket.dto.promotion.PromotionRequest;
import com.yolge.supermarket.dto.promotion.PromotionResponse;
import com.yolge.supermarket.entity.Product;
import com.yolge.supermarket.entity.Promotion;
import com.yolge.supermarket.util.FormatDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PromotionMapper {

    public Promotion toEntity(PromotionRequest request, List<Product> products) {
        Promotion promotion = new Promotion();
        mapRequestToEntity(request, promotion, products);
        return promotion;
    }

    public PromotionResponse toDto(Promotion promotion) {
        PromotionResponse dto = new PromotionResponse();
        dto.setId(promotion.getId());
        dto.setName(promotion.getName());
        dto.setDiscountPercentage(promotion.getDiscountPercentage());
        dto.setStartDate(FormatDate.formatToIso(promotion.getStartDate()));
        dto.setEndDate(FormatDate.formatToIso(promotion.getEndDate()));
        dto.setCreatedAt(promotion.getCreatedAt());
        return dto;
    }

    public List<PromotionResponse> toDtoList(List<Promotion> promotions) {
        return promotions.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private void mapRequestToEntity(PromotionRequest request, Promotion promotion, List<Product> products) {
        promotion.setName(request.getName());
        promotion.setDiscountPercentage(request.getDiscountPercentage());

        if (request.getStartDate() != null) {
            promotion.setStartDate(FormatDate.formatFromIso(request.getStartDate()));
        }
        if (request.getEndDate() != null) {
            promotion.setEndDate(FormatDate.formatFromIso(request.getEndDate()));
        }

        if (products != null && !products.isEmpty()) {
            promotion.setProducts(products);
        } else {
            promotion.getProducts().clear();
        }
    }
}