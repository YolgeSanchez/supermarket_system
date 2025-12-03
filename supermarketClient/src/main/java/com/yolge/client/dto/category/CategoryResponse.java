package com.yolge.client.dto.category;

import com.yolge.client.dto.product.ProductResponse;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private List<ProductResponse> products;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
