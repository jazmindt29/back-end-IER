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
    private final BrevoClient brevo = mock(BrevoClient.class);
    private final MailService mail = new MailService(sender, brevo);

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

    @Test void usaBrevoCuandoEstaConfigurado() {
        ReflectionTestUtils.setField(mail, "frontendUrl", "https://ier.example.org");
        when(brevo.configurado()).thenReturn(true);
        when(brevo.enviar(anyString(), anyString(), anyString(), anyString())).thenReturn(true);

        assertThat(mail.enviarInvitacion("test@example.invalid", "Prueba", "token-test")).isTrue();

        verify(brevo).enviar(eq("test@example.invalid"), eq("Prueba"), anyString(),
                argThat(cuerpo -> cuerpo.contains("https://ier.example.org/configurar-cuenta?token=token-test")));
        verifyNoInteractions(sender);
    }

    @Test void usaLaUrlPublicaDeRenderSiFaltaAppFrontendUrl() {
        ReflectionTestUtils.setField(mail, "frontendUrl", "");
        ReflectionTestUtils.setField(mail, "renderExternalUrl", "https://ier.onrender.com");
        when(brevo.configurado()).thenReturn(true);
        when(brevo.enviar(anyString(), anyString(), anyString(), anyString())).thenReturn(true);

        assertThat(mail.enviarInvitacion("test@example.invalid", "Prueba", "token-test")).isTrue();

        verify(brevo).enviar(eq("test@example.invalid"), eq("Prueba"), anyString(),
                argThat(cuerpo -> cuerpo.contains("https://ier.onrender.com/configurar-cuenta?token=token-test")));
    }
}
