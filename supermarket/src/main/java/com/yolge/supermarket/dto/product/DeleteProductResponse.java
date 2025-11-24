package com.yolge.supermarket.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteProductResponse {
    private String message;
    private Long deletedProductId;
}
