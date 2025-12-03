package com.yolge.client.dto.promotion;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeletePromotionResponse {
    private String message;
    private Long deletedPromotionId;
}
