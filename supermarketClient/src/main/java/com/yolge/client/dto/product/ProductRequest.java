package com.yolge.client.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    private String name;
    private String brand;
    private Double basePrice;
    private Double taxPercentage;
    private Long categoryId;
    private Integer stock;
}
