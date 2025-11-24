package com.yolge.supermarket.dto.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ValidationErrorDto {
    private int status;
    private String message;
    private List<String> errors;
    private LocalDateTime timestamp;
}