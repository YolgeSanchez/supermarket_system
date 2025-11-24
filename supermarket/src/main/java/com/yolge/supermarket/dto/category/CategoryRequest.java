package com.yolge.supermarket.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryRequest {
    @NotBlank(message = "El nombre de la categoría no puede estar vacío")
    @Size(min = 3, max = 50, message = "El nombre de la categoría debe tener entre 3 y 50 caracteres")
    private String name;

    @NotBlank(message = "La descripción de la categoría no puede estar vacía")
    @Size(min = 20, max = 255, message = "La descripción de la categoría debe tener entre 20 y 255 caracteres")
    private String description;
}
