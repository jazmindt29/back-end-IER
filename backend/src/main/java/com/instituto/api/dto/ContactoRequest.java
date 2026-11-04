package com.instituto.api.dto;

import com.instituto.api.entity.Contacto;
import jakarta.validation.constraints.*;

public record ContactoRequest(
        @Null(message = "No se permite indicar un ID al crear") Long id,
        @NotBlank @Size(max = 200) String nombre,
        @NotBlank @Email @Size(max = 254) String email,
        @Size(max = 200) String asunto,
        @NotBlank @Size(max = 10000) String mensaje) {
    public Contacto toEntity() {
        Contacto contacto = new Contacto();
        contacto.setNombre(nombre);
        contacto.setEmail(email);
        contacto.setAsunto(asunto);
        contacto.setMensaje(mensaje);
        return contacto;
    }
}
