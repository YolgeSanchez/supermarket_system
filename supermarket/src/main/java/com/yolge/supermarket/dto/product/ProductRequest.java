package com.yolge.supermarket.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProductRequest {
    @NotBlank(message = "El nombre del producto es obligatorio")
    private String name;

    private String brand;

    @NotNull(message = "El precio base es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    private Double basePrice;

    @NotNull(message = "El porcentaje de impuesto es obligatorio")
    @Min(value = 0, message = "El impuesto no puede ser negativo")
    private Double taxPercentage;

    @NotNull(message = "Debes seleccionar una categoría")
    @Min(value = 0, message = "El ID de la categoria no puede ser negativo")
    private Long categoryId;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;
}
