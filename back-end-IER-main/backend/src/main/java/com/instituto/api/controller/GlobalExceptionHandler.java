package com.instituto.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Traduce las excepciones de negocio a códigos HTTP consistentes con un
 * cuerpo JSON {"mensaje": ...} que el frontend puede mostrar directamente.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> datosInvalidos(MethodArgumentNotValidException ex) {
        String detalle = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + mensajeDe(e))
                .collect(Collectors.joining("; "));
        return Map.of("mensaje", "Datos inválidos: " + detalle);
    }

    private String mensajeDe(FieldError e) {
        return e.getDefaultMessage() == null ? "valor inválido" : e.getDefaultMessage();
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> noEncontrado(NoSuchElementException ex) {
        return Map.of("mensaje", ex.getMessage() == null ? "Recurso no encontrado" : ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> peticionInvalida(IllegalArgumentException ex) {
        return Map.of("mensaje", ex.getMessage());
    }

    // Conflictos de negocio, ej. colisión de horarios en reservas
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> conflicto(IllegalStateException ex) {
        return Map.of("mensaje", ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, String> credenciales(BadCredentialsException ex) {
        return Map.of("mensaje", "Usuario o contraseña incorrectos");
    }
}
