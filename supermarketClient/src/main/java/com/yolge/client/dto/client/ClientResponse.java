package com.yolge.client.dto.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientResponse {
    private Long id;
    private String name;
    private String dni;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
