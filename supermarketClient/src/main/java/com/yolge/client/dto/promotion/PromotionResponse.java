package com.yolge.client.dto.promotion;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotionResponse {
    private Long id;
    private String name;
    private Double discountPercentage;
    private String startDate;
    private String endDate;
    private LocalDateTime createdAt;
}
