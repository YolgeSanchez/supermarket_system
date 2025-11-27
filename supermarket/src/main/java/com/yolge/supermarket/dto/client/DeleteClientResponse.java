package com.yolge.supermarket.dto.client;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteClientResponse {
    private String message;
    private Long deletedClientId;
}
