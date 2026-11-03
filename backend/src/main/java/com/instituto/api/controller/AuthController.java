package com.instituto.api.controller;

import com.instituto.api.dto.ConfigurarCuentaRequest;
import com.instituto.api.dto.InvitacionRequest;
import com.instituto.api.dto.LoginRequest;
import com.instituto.api.dto.LoginResponse;
import com.instituto.api.entity.Usuario;
import com.instituto.api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    // El admin invita a un investigador: solo nombre + correo institucional.
    // El backend crea la cuenta inactiva y envía el enlace de activación por email.
    @PostMapping("/invitaciones")
    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRADOR')")
    public ResponseEntity<Map<String, String>> invitar(@Valid @RequestBody InvitacionRequest request) {
        authService.invitarInvestigador(request);
        return new ResponseEntity<>(Map.of("mensaje", "Invitación enviada a " + request.email()), HttpStatus.CREATED);
    }

  @GetMapping("/invitaciones/{token}")
    public ResponseEntity<?> validarToken(@PathVariable String token) {
        try {
            Usuario usuario = authService.validarToken(token);
            return ResponseEntity.ok(Map.of("valido", true, "email", usuario.getUsername()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("valido", false, "mensaje", e.getMessage()));
        }
    }

    @PostMapping("/configurar-cuenta")
    public ResponseEntity<?> configurarCuenta(@Valid @RequestBody ConfigurarCuentaRequest request) {
        try {
            authService.configurarCuenta(request);
            return ResponseEntity.ok(Map.of("mensaje", "Cuenta activada. Ya puedes iniciar sesión."));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }
}