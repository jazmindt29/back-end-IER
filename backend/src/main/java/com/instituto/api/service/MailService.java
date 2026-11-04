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

    @Value("${spring.mail.username}")
    private String remitente;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.mail.contacto-destino}")
    private String contactoDestino;

    public MailService(JavaMailSender sender) {
        this.sender = sender;
    }

    public boolean enviarInvitacion(String email, String nombre, String token) {
        if (frontendUrl == null || frontendUrl.isBlank()) {
            log.warn("APP_FRONTEND_URL no configurado; invitación guardada sin enviar enlace");
            return false;
        }
        String base = frontendUrl.trim();
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
        return enviar(email, "Invitación a la plataforma del IER", cuerpo);
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
        enviar(contactoDestino, "Contacto IER: " + (contacto.getAsunto() == null ? "nuevo mensaje" : contacto.getAsunto()), cuerpo);
    }

    // No registrar cuerpos, destinatarios ni tokens; el llamador informa el resultado.
    private boolean enviar(String para, String asunto, String cuerpo) {
        if (remitente == null || remitente.isBlank()) {
            log.warn("SMTP sin configurar; correo no enviado");
            return false;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            if (remitente != null && !remitente.isBlank()) {
                msg.setFrom(remitente);
            }
            msg.setTo(para);
            msg.setSubject(asunto);
            msg.setText(cuerpo);
            sender.send(msg);
            log.info("Correo enviado correctamente");
            return true;
        } catch (Exception e) {
            log.warn("No se pudo enviar el correo ({})", e.getClass().getSimpleName());
            return false;
        }
    }
}
