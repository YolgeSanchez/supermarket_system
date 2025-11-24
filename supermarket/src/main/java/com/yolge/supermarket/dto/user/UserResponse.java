package com.yolge.supermarket.dto.user;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonPropertyOrder({
    "userId",
    "fullName",
    "username",
    "role",
    "createdAt",
    "updatedAt"
})
public class UserResponse {
    private Long userId;
    private String fullName;
    private String username;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
