package com.yolge.supermarket.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductStockRequest {
    @NotNull @Min(1)
    private Integer quantity;
}