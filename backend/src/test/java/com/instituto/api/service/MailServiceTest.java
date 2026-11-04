package com.instituto.api.service;

import org.junit.jupiter.api.Test;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class MailServiceTest {
    private final JavaMailSender sender = mock(JavaMailSender.class);
    private final MailService mail = new MailService(sender);

    @Test void sinSmtpNoIntentaEnviarYReportaFallo() {
        ReflectionTestUtils.setField(mail, "frontendUrl", "http://localhost:4200");
        assertThat(mail.enviarInvitacion("test@example.invalid", "Prueba", "token-test")).isFalse();
        verifyNoInteractions(sender);
    }

    @Test void sinUrlPublicaNoEnviaUnEnlaceRoto() {
        ReflectionTestUtils.setField(mail, "frontendUrl", "");
        ReflectionTestUtils.setField(mail, "remitente", "sender@example.invalid");
        assertThat(mail.enviarInvitacion("test@example.invalid", "Prueba", "token-test")).isFalse();
        verifyNoInteractions(sender);
    }

    @Test void falloSmtpNoSePresentaComoEnvioExitoso() {
        ReflectionTestUtils.setField(mail, "frontendUrl", "http://localhost:4200");
        ReflectionTestUtils.setField(mail, "remitente", "sender@example.invalid");
        doThrow(new MailSendException("No disponible")).when(sender).send(any(SimpleMailMessage.class));
        assertThat(mail.enviarInvitacion("test@example.invalid", "Prueba", "token-test")).isFalse();
    }

    @Test void envioExitosoContieneEnlaceCorrecto() {
        ReflectionTestUtils.setField(mail, "frontendUrl", "http://localhost:4200");
        ReflectionTestUtils.setField(mail, "remitente", "sender@example.invalid");
        assertThat(mail.enviarInvitacion("test@example.invalid", "Prueba", "token-test")).isTrue();
        var message = org.mockito.ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(sender).send(message.capture());
        assertThat(message.getValue().getText()).contains("http://localhost:4200/configurar-cuenta?token=token-test");
    }
}
