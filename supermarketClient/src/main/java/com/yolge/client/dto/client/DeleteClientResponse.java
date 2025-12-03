package com.yolge.client.dto.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteClientResponse {
    private String message;
    private Long deletedClientId;
}
