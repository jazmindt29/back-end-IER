package com.instituto.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

/** La API recibe identificadores, nunca entidades JPA construidas por el cliente. */
public record ProyectoRequest(
        @NotBlank @Size(max = 255) String titulo,
        String descripcion,
        @Size(max = 255) String estado,
        Boolean esDestacado,
        @Size(max = 255) String imagenUrl,
        @NotNull List<@NotNull @Positive Long> investigadorIds) { }
