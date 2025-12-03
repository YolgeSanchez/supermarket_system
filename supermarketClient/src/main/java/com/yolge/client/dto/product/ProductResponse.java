package com.yolge.client.dto.product;

import com.yolge.client.dto.category.CategorySlimResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
