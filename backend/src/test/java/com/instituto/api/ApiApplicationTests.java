package com.instituto.api;

import org.junit.jupiter.api.Test;
import java.util.Properties;
import static org.assertj.core.api.Assertions.*;

/** Verifica defaults sin arrancar Spring ni acceder a una base externa. */
class ApiApplicationTests {
    @Test void configuracionComunNoIncluyeCredencialesOperativas() throws Exception {
        Properties properties = new Properties();
        try (var input = getClass().getResourceAsStream("/application.properties")) {
            properties.load(input);
        }
        assertThat(properties.getProperty("spring.mail.password")).isEqualTo("${SPRING_MAIL_PASSWORD:}");
        assertThat(properties.getProperty("app.jwt.secret")).isEqualTo("${JWT_SECRET:}");
        assertThat(properties.getProperty("spring.jpa.hibernate.ddl-auto")).isEqualTo("validate");
    }
}
