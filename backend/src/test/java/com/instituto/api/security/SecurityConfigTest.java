package com.instituto.api.security;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import static org.assertj.core.api.Assertions.*;

class SecurityConfigTest {
    @Test void exigeUnaClavePropiaDeLongitudSuficiente() {
        SecurityConfig config = new SecurityConfig();
        ReflectionTestUtils.setField(config, "jwtSecret", "");
        assertThatThrownBy(config::jwtEncoder).isInstanceOf(IllegalStateException.class);
        ReflectionTestUtils.setField(config, "jwtSecret", "ier-secreto-de-desarrollo-cambiar-en-produccion!");
        assertThatThrownBy(config::jwtEncoder).isInstanceOf(IllegalStateException.class);
        ReflectionTestUtils.setField(config, "jwtSecret", "test-unitario-una-clave-distinta-de-32-bytes");
        assertThatCode(config::jwtEncoder).doesNotThrowAnyException();
    }
}
