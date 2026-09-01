package com.instituto.api.service;

import com.instituto.api.entity.Contacto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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

    public void enviarInvitacion(String email, String nombre, String token) {
        String enlace = frontendUrl + "/configurar-cuenta?token=" + token;
        String cuerpo = """
                Hola %s:

                Se te ha creado una cuenta de investigador en la plataforma del IER.
                Para activarla y establecer tu contraseña, entra al siguiente enlace
                (válido por 48 horas):

                %s

                Si no esperabas este correo, ignóralo.
                """.formatted(nombre, enlace);
        enviar(email, "Invitación a la plataforma del IER", cuerpo);
    }

    public void notificarContacto(Contacto contacto) {
        if (contactoDestino == null || contactoDestino.isBlank()) {
            log.info("APP_MAIL_CONTACTO no configurado; mensaje de contacto de {} solo guardado en BD", contacto.getEmail());
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

    // ponytail: si el SMTP no está configurado o falla, se registra en el log y la
    // operación de negocio (guardar en BD) no se revierte; el enlace queda en el log
    private void enviar(String para, String asunto, String cuerpo) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            if (remitente != null && !remitente.isBlank()) {
                msg.setFrom(remitente);
            }
            msg.setTo(para);
            msg.setSubject(asunto);
            msg.setText(cuerpo);
            sender.send(msg);
            log.info("Correo enviado a {}: {}", para, asunto);
        } catch (Exception e) {
            log.warn("No se pudo enviar el correo a {} ({}). Contenido:\n{}", para, e.getMessage(), cuerpo);
        }
    }
}
