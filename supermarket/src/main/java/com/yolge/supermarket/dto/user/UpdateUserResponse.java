package com.yolge.supermarket.dto.user;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonPropertyOrder({"message", "updatedUserId"})
public class UpdateUserResponse {
    private String message;
    private Long updatedUserId;
}
