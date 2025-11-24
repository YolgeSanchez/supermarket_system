package com.yolge.supermarket.dto.user;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonPropertyOrder({"message", "deletedUserId"})
public class DeleteUserResponse {
    private String message;
    private Long deletedUserId;
}
