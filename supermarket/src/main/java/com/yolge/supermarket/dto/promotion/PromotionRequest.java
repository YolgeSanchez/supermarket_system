package com.yolge.supermarket.dto.promotion;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PromotionRequest {
    @NotBlank(message = "El nombre de la promoción es obligatorio.")
    @Size(max = 255, message = "El nombre no puede exceder los 255 caracteres.")
    private String name;

    @NotNull(message = "El porcentaje de descuento es obligatorio.")
    @DecimalMin(value = "0.01", message = "El descuento debe ser mayor a 0.")
    @DecimalMax(value = "100.00", message = "El descuento no puede ser mayor al 100%.")
    private Double discountPercentage;

    @NotBlank(message = "La fecha de inicio es obligatoria (formato YYYY-MM-DD).")
    private String startDate;

    @NotBlank(message = "La fecha de fin es obligatoria (formato YYYY-MM-DD).")
    private String endDate;

    @NotEmpty(message = "La promoción debe estar asignada a al menos un producto.")
    @NotNull(message = "El listado de productIds es obligatorio.")
    private Long[] productIds;
}
