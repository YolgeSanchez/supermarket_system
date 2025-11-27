package com.yolge.supermarket.dto.client;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonPropertyOrder({
    "id",
    "name",
    "dni",
    "email",
    "createdAt",
    "updatedAt"
})
public class ClientResponse {
    private Long id;
    private String name;
    private String dni;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
