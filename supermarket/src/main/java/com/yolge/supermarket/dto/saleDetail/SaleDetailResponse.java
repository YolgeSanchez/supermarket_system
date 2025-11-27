package com.yolge.supermarket.dto.saleDetail;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.yolge.supermarket.dto.product.ProductResponse;
import lombok.Data;

@Data
@JsonPropertyOrder({
    "id",
    "product",
    "quantity",
    "unitPrice",
    "subTotal"
})
public class SaleDetailResponse {
    private Long id;
    private ProductResponse product;
    private Integer quantity;
    private Double unitPrice;
    private Double subTotal;
}