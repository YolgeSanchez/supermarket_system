package com.yolge.client.dto.category;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteCategoryResponse {
    private String message;
    private Long deletedCategoryId;
}
