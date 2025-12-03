package com.yolge.client.dto.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteCategoryResponse {
    private String message;
    private Long deletedCategoryId;
}
