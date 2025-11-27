package com.yolge.supermarket.exceptions;

import com.yolge.supermarket.dto.error.ErrorDto;
import com.yolge.supermarket.dto.error.ValidationErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDto> handleNotFound(NotFoundException ex, HttpServletRequest request) {
        ErrorDto error = new ErrorDto(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "404 NOT_FOUND",
                ex.getMessage(),
                request.getRequestURI(),
                ex.getClass().getName()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorDto> handleConflict(ConflictException ex, HttpServletRequest request) {
        ErrorDto error = new ErrorDto(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "409 CONFLICT",
                ex.getMessage(),
                request.getRequestURI(),
                ex.getClass().getName()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorDto> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ValidationErrorDto response = new ValidationErrorDto(
                HttpStatus.BAD_REQUEST.value(),
                "400 BAD_REQUEST",
                errors,
                LocalDateTime.now()
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorDto> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        ErrorDto error = new ErrorDto(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "400 BAD_REQUEST",
                "Tipo de dato incorrecto para el parámetro: " + ex.getName(),
                request.getRequestURI(),
                ex.getClass().getName()
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorDto> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        ErrorDto error = new ErrorDto(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "401 UNAUTHORIZED",
                "Credenciales invalidas. Por favor, intente nuevamente.",
                request.getRequestURI(),
                ex.getClass().getName()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorDto> handleResponseStatus(ResponseStatusException ex, HttpServletRequest request) {
        ErrorDto error = new ErrorDto(
                LocalDateTime.now(),
                ex.getStatusCode().value(),
                ex.getStatusCode().toString(),
                ex.getReason(),
                request.getRequestURI(),
                ex.getClass().getName()
        );

        return ResponseEntity.status(ex.getStatusCode()).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDto> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        ErrorDto error = new ErrorDto(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "400 BAD_REQUEST",
                "Solicitud invalida. Por favor, revise los valores ingresados.",
                request.getRequestURI(),
                ex.getClass().getName()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDto> handleBadRequestException(BadRequestException ex, HttpServletRequest request) {
        ErrorDto error = new ErrorDto(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "400 BAD_REQUEST",
                ex.getMessage(),
                request.getRequestURI(),
                ex.getClass().getName()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorDto> handleMissingParameter(MissingServletRequestParameterException ex, HttpServletRequest request) {
        ErrorDto error = new ErrorDto(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "400 BAD_REQUEST",
                "Falta el parámetro requerido: " + ex.getParameterName(),
                request.getRequestURI(),
                ex.getClass().getName()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorDto> handleAuthorizationDenied(AuthorizationDeniedException ex, HttpServletRequest request) {
        ErrorDto error = new ErrorDto(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                "403 FORBIDDEN",
                "Acceso denegado. No tiene permiso para realizar esta acción.",
                request.getRequestURI(),
                ex.getClass().getName()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleException(Exception ex, HttpServletRequest request) {
        ErrorDto error = new ErrorDto(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "500 INTERNAL_SERVER_ERROR",
                "Unexpected: " + ex.getMessage(),
                request.getRequestURI(),
                ex.getClass().getName()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

