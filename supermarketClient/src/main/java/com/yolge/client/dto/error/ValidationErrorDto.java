package com.yolge.client.dto.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorDto {
    private int status;
    private String message;
    private List<String> errors;
    private LocalDateTime timestamp;
}