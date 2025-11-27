package com.yolge.supermarket.dto.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClientRequest {
    @NotBlank(message = "El nombre del cliente es obligatorio.")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres.")
    private String name;

    @NotBlank(message = "La cédula/DNI es obligatoria.")
    @Size(min = 10, max = 10, message = "El documento debe tener 10 caracteres.")
    @Pattern(regexp = "\\d+", message = "El documento solo debe contener números.")
    private String dni;

    @NotBlank(message = "El correo electrónico es obligatorio.")
    @Email(message = "El formato del correo electrónico no es válido.")
    private String email;
}
