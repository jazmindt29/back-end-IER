package com.instituto.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record InvitacionRequest(
        @NotBlank @jakarta.validation.constraints.Size(max = 200) String nombre,
        @NotBlank @Email @jakarta.validation.constraints.Size(max = 254) String email) {
}
