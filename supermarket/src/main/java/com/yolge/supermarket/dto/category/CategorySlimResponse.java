package com.yolge.supermarket.dto.category;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({"id", "name"})
public class CategorySlimResponse {
    private Long id;
    private String name;
}
