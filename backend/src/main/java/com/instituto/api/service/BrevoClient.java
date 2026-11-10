package com.instituto.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Envío de correos por la API HTTPS de Brevo (puerto 443).
 *
 * Render bloquea los puertos SMTP 25, 465 y 587 en los servicios gratuitos,
 * así que esta es la vía que funciona sin cambiar de plan. Si no hay API key
 * o remitente configurado, MailService usa SMTP como respaldo.
 */
@Component
public class BrevoClient {

    private static final Logger log = LoggerFactory.getLogger(BrevoClient.class);

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final ObjectMapper json = new ObjectMapper();

    private final String apiKey;
    private final String apiUrl;
    private final String remitente;
    private final String remitenteNombre;

    public BrevoClient(
            @Value("${app.mail.brevo-api-key:}") String apiKey,
            @Value("${app.mail.brevo-url:https://api.brevo.com/v3/smtp/email}") String apiUrl,
            @Value("${app.mail.remitente:}") String remitente,
            @Value("${app.mail.remitente-nombre:IER}") String remitenteNombre) {
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.apiUrl = apiUrl == null || apiUrl.isBlank()
                ? "https://api.brevo.com/v3/smtp/email"
                : apiUrl.trim();
        this.remitente = remitente == null ? "" : remitente.trim();
        this.remitenteNombre = remitenteNombre == null || remitenteNombre.isBlank()
                ? "IER"
                : remitenteNombre.trim();
    }

    public boolean configurado() {
        return !apiKey.isBlank() && !remitente.isBlank();
    }

    public boolean enviar(String para, String nombre, String asunto, String cuerpo) {
        if (!configurado()) {
            return false;
        }
        try {
            Map<String, Object> destino = new LinkedHashMap<>();
            destino.put("email", para);
            if (nombre != null && !nombre.isBlank()) {
                destino.put("name", nombre);
            }

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("sender", Map.of("email", remitente, "name", remitenteNombre));
            payload.put("to", List.of(destino));
            payload.put("subject", asunto);
            payload.put("textContent", cuerpo);

            HttpRequest request = HttpRequest.newBuilder(URI.create(apiUrl))
                    .timeout(Duration.ofSeconds(15))
                    .header("api-key", apiKey)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json.writeValueAsString(payload)))
                    .build();

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return true;
            }
            log.warn("Brevo rechazó el correo (HTTP {}); revisa remitente verificado y API key", response.statusCode());
            return false;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.warn("Envío por Brevo interrumpido");
            return false;
        } catch (Exception ex) {
            log.warn("No se pudo enviar el correo por Brevo ({})", ex.getClass().getSimpleName());
            return false;
        }
    }
}
