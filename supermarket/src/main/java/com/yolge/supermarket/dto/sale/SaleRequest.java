package com.yolge.supermarket.dto.sale;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SaleRequest {
    @Positive(message = "El ID del cliente debe ser válido.")
    private Long clientId;
}
