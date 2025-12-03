package com.yolge.client.dto.category;

import com.yolge.client.dto.product.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private List<ProductResponse> products;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
