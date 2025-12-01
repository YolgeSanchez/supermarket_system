package com.yolge.client.exceptions;

import java.util.List;

public class ApiValidationException extends ApiException {
    private final List<String> errors;
    public ApiValidationException(List<String> errors) {
        super("Error de validacion", 400);
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
