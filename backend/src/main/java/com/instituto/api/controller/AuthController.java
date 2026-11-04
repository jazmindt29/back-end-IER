package com.instituto.api.controller;

import com.instituto.api.dto.*;
import com.instituto.api.service.AuthService;
import com.instituto.api.service.MailService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService auth;
    private final MailService mail;

    public AuthController(AuthService auth, MailService mail) {
        this.auth = auth;
        this.mail = mail;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return auth.login(request);
    }

    @PostMapping("/invitaciones")
    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRADOR')")
    public ResponseEntity<ResultadoInvitacion> invitar(@Valid @RequestBody InvitacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(prepararYEnviar(request, false));
    }

    @PostMapping("/invitaciones/reenviar")
    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRADOR')")
    public ResultadoInvitacion reenviar(@Valid @RequestBody InvitacionRequest request) {
        return prepararYEnviar(request, true);
    }

    private ResultadoInvitacion prepararYEnviar(InvitacionRequest request, boolean reenviar) {
        var invitacion = auth.prepararInvitacion(request, reenviar);
        // La transacción del servicio ya terminó: el enlace existe antes de enviarlo.
        boolean enviado = mail.enviarInvitacion(invitacion.email(), invitacion.nombre(), invitacion.token());
        String mensaje = enviado
                ? "Invitación enviada. El enlace tiene una vigencia de 48 horas."
                : "Invitación guardada, pero el correo no pudo enviarse. Revisa SMTP y usa Reenviar invitación.";
        return new ResultadoInvitacion(mensaje, enviado, invitacion.venceEn());
    }

    @GetMapping("/invitaciones/{token}")
    public Map<String, Object> validarToken(@PathVariable String token) {
        var usuario = auth.validarToken(token);
        return Map.of("valido", true, "email", usuario.getUsername());
    }

    @PostMapping("/configurar-cuenta")
    public Map<String, String> configurarCuenta(@Valid @RequestBody ConfigurarCuentaRequest request) {
        auth.configurarCuenta(request);
        return Map.of("mensaje", "Cuenta activada. Ya puedes iniciar sesión.");
    }

    public record ResultadoInvitacion(String mensaje, boolean enviado, LocalDateTime venceEn) { }
}
