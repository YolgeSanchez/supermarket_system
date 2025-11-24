package com.yolge.supermarket.dto.auth;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(min = 10, max = 100, message = "El nombre debe tener entre 10 y 100 caracteres")
    private String fullName;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 4, max = 50, message = "El nombre de usuario debe tener entre 4 y 100 caracteres")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
    private String password;

    @Pattern(regexp = "(?i)^(CASHIER|ADMIN|INVENTORY)$", message = "El rol es invalido")
    private String role;
}
