package com.yolge.client.dto.promotion;

import lombok.Data;

@Data
public class PromotionRequest {
    private String name;
    private Double discountPercentage;
    private String startDate;
    private String endDate;
    private Long[] productIds;
}
