package com.instituto.api.service;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class BrevoClientTest {

    private HttpServer server;

    @AfterEach
    void detenerServidor() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void enviaElCuerpoYLaApiKeyEsperados() throws Exception {
        AtomicReference<String> cuerpo = new AtomicReference<>();
        AtomicReference<String> apiKey = new AtomicReference<>();

        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/v3/smtp/email", exchange -> {
            cuerpo.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            apiKey.set(exchange.getRequestHeaders().getFirst("api-key"));
            exchange.sendResponseHeaders(201, -1);
            exchange.close();
        });
        server.start();

        String url = "http://127.0.0.1:" + server.getAddress().getPort() + "/v3/smtp/email";
        BrevoClient client = new BrevoClient("clave-prueba", url, "remitente@example.invalid", "IER");

        assertThat(client.configurado()).isTrue();
        assertThat(client.enviar("destino@example.invalid", "Destino", "Asunto", "Cuerpo del correo")).isTrue();
        assertThat(apiKey.get()).isEqualTo("clave-prueba");
        assertThat(cuerpo.get())
                .contains("\"email\":\"remitente@example.invalid\"")
                .contains("\"email\":\"destino@example.invalid\"")
                .contains("\"subject\":\"Asunto\"")
                .contains("\"textContent\":\"Cuerpo del correo\"");
    }

    @Test
    void unErrorHttpNoSePresentaComoEnvioExitoso() throws Exception {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/v3/smtp/email", exchange -> {
            exchange.getRequestBody().readAllBytes();
            exchange.sendResponseHeaders(400, -1);
            exchange.close();
        });
        server.start();

        String url = "http://127.0.0.1:" + server.getAddress().getPort() + "/v3/smtp/email";
        BrevoClient client = new BrevoClient("clave-prueba", url, "remitente@example.invalid", "IER");

        assertThat(client.enviar("destino@example.invalid", "Destino", "Asunto", "Cuerpo")).isFalse();
    }

    @Test
    void sinApiKeyORemitenteNoEstaConfigurado() {
        assertThat(new BrevoClient("", "https://api.brevo.com/v3/smtp/email",
                "remitente@example.invalid", "IER").configurado()).isFalse();
        assertThat(new BrevoClient("clave", "https://api.brevo.com/v3/smtp/email",
                "", "IER").configurado()).isFalse();
    }
}
