package com.yolge.supermarket.dto.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.yolge.supermarket.dto.category.CategorySlimResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonPropertyOrder({
    "id",
    "name",
    "brand",
    "category",
    "stock",
    "basePrice",
    "taxPercentage",
    "finalPrice",
    "createdAt",
    "updatedAt"
})
@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String brand;
    private Double basePrice;
    private Double taxPercentage;
    private BigDecimal finalPrice;
    private Integer stock;
    private CategorySlimResponse category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
