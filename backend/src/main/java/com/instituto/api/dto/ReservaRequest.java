package com.instituto.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;

public record ReservaRequest(
        @Null(message = "No se permite indicar un ID al crear") Long id,
        @NotNull @FutureOrPresent LocalDate fecha,
        @NotNull LocalTime horaInicio,
        @NotNull LocalTime horaFin,
        @NotNull @Valid Referencia laboratorio,
        @Valid Referencia investigador) {
    public record Referencia(@NotNull @Positive Long id) { }
}
