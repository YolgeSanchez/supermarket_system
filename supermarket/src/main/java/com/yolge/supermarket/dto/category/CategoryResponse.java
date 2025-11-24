package com.yolge.supermarket.dto.category;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.yolge.supermarket.dto.product.ProductResponse;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonPropertyOrder({
    "id",
    "name",
    "description",
    "products",
    "createdAt",
    "updatedAt"
})
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private List<ProductResponse> products;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
