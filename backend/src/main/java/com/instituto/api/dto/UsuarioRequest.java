package com.instituto.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record UsuarioRequest(
        @Null Long id,
        @NotBlank @Size(max = 200) String username,
        @NotBlank @Size(min = 12, max = 72) String password,
        @NotNull @Valid ReservaRequest.Referencia rol) { }
