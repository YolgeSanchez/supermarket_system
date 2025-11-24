package com.yolge.supermarket.dto.category;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonPropertyOrder({"message", "deletedCategoryId"})
public class DeleteCategoryResponse {
    private String message;
    private Long deletedCategoryId;
}
