package com.yolge.supermarket.dto.promotion;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonPropertyOrder({
    "id",
    "name",
    "discountPercentage",
    "startDate",
    "endDate",
    "createdAt"
})
public class PromotionResponse {
    private Long id;
    private String name;
    private Double discountPercentage;
    private String startDate;
    private String endDate;
    private LocalDateTime createdAt;
}
