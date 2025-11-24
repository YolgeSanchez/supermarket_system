package com.yolge.supermarket.dto.promotion;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonPropertyOrder({"message", "deletedPromotionId"})
public class DeletePromotionResponse {
    private String message;
    private Long deletedPromotionId;
}
