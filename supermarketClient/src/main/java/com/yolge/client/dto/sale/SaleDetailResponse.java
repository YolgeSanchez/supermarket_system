package com.yolge.client.dto.sale;

import com.yolge.client.dto.product.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaleDetailResponse {
    private Long id;
    private ProductResponse product;
    private Integer quantity;
    private Double unitPrice;
    private Double subTotal;
}