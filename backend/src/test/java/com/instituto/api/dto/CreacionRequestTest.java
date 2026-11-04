package com.instituto.api.dto;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import static org.assertj.core.api.Assertions.*;

class CreacionRequestTest {
    @Test void contactoNoAceptaIdYSiempreConstruyeUnaEntidadNueva() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var solicitud = new ContactoRequest(5L, "Prueba", "test@example.org", "Asunto", "Mensaje");
            assertThat(factory.getValidator().validate(solicitud)).isNotEmpty();
            assertThat(solicitud.toEntity().getId()).isNull();
        }
    }

    @Test void reservaRechazaIdYReferenciasInvalidas() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var solicitud = new ReservaRequest(2L, LocalDate.now(), LocalTime.NOON,
                    LocalTime.of(13, 0), new ReservaRequest.Referencia(-1L), null);
            assertThat(factory.getValidator().validate(solicitud)).hasSize(2);
        }
    }
}
