package com.instituto.api.service;

import com.instituto.api.entity.Contacto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.net.URI;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender sender;
    private final BrevoClient brevo;

    @Value("${spring.mail.username}")
    private String remitente;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    // Render expone la URL pública del servicio aunque APP_FRONTEND_URL llegue vacío.
    @Value("${RENDER_EXTERNAL_URL:}")
    private String renderExternalUrl;

    @Value("${app.mail.contacto-destino}")
    private String contactoDestino;

    public MailService(JavaMailSender sender, BrevoClient brevo) {
        this.sender = sender;
        this.brevo = brevo;
    }

    public boolean enviarInvitacion(String email, String nombre, String token) {
        String base = urlPublica();
        if (base == null) {
            log.warn("APP_FRONTEND_URL no configurado; invitación guardada sin enviar enlace");
            return false;
        }
        try {
            URI uri = URI.create(base);
            if (uri.getHost() == null || !("http".equalsIgnoreCase(uri.getScheme())
                    || "https".equalsIgnoreCase(uri.getScheme())) || uri.getQuery() != null
                    || uri.getFragment() != null) {
                log.warn("APP_FRONTEND_URL no es una URL HTTP(S) válida; correo no enviado");
                return false;
            }
        } catch (IllegalArgumentException ex) {
            log.warn("APP_FRONTEND_URL no es una URL HTTP(S) válida; correo no enviado");
            return false;
        }
        String enlace = base.replaceFirst("/+$", "") + "/configurar-cuenta?token=" + token;
        String cuerpo = """
                Hola %s:

                Se te ha creado una cuenta de investigador en la plataforma del IER.
                Para activarla y establecer tu contraseña, entra al siguiente enlace
                (válido por 48 horas):

                %s

                Si no esperabas este correo, ignóralo.
                """.formatted(nombre, enlace);
        return enviar(email, nombre, "Invitación a la plataforma del IER", cuerpo);
    }

    public void notificarContacto(Contacto contacto) {
        if (contactoDestino == null || contactoDestino.isBlank()) {
            log.info("APP_MAIL_CONTACTO no configurado; mensaje de contacto guardado sin notificación");
            return;
        }
        String cuerpo = """
                Nuevo mensaje desde el formulario de contacto:

                Nombre: %s
                Correo: %s
                Asunto: %s

                %s
                """.formatted(contacto.getNombre(), contacto.getEmail(),
                contacto.getAsunto() == null ? "(sin asunto)" : contacto.getAsunto(),
                contacto.getMensaje());
        enviar(contactoDestino, null,
                "Contacto IER: " + (contacto.getAsunto() == null ? "nuevo mensaje" : contacto.getAsunto()), cuerpo);
    }

    private String urlPublica() {
        if (frontendUrl != null && !frontendUrl.isBlank()) {
            return frontendUrl.trim();
        }
        if (renderExternalUrl != null && !renderExternalUrl.isBlank()) {
            return renderExternalUrl.trim();
        }
        return null;
    }

    // No registrar cuerpos, destinatarios ni tokens; el llamador informa el resultado.
    private boolean enviar(String para, String nombre, String asunto, String cuerpo) {
        // Brevo por HTTPS funciona en el plan gratuito de Render.
        if (brevo.configurado()) {
            boolean enviado = brevo.enviar(para, nombre, asunto, cuerpo);
            if (enviado) {
                log.info("Correo enviado correctamente por Brevo");
            }
            return enviado;
        }

        if (remitente == null || remitente.isBlank()) {
            log.warn("Ni Brevo ni SMTP están configurados; correo no enviado");
            return false;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(remitente);
            msg.setTo(para);
            msg.setSubject(asunto);
            msg.setText(cuerpo);
            sender.send(msg);
            log.info("Correo enviado correctamente por SMTP");
            return true;
        } catch (Exception e) {
            log.warn("No se pudo enviar el correo por SMTP ({})", e.getClass().getSimpleName());
            return false;
        }
    }
}
