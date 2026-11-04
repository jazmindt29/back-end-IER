package com.instituto.api.service;

import com.instituto.api.dto.*;
import com.instituto.api.entity.*;
import com.instituto.api.repository.*;
import com.instituto.api.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class AuthService {
    private final UsuarioRepository usuarios;
    private final InvestigadorRepository investigadores;
    private final RolRepository roles;
    private final PasswordEncoder passwords;
    private final JwtService jwt;

    public AuthService(UsuarioRepository usuarios, InvestigadorRepository investigadores,
                       RolRepository roles, PasswordEncoder passwords, JwtService jwt) {
        this.usuarios = usuarios;
        this.investigadores = investigadores;
        this.roles = roles;
        this.passwords = passwords;
        this.jwt = jwt;
    }

    public LoginResponse login(LoginRequest request) {
        String identificador = request.username().trim();
        if (identificador.contains("@")) identificador = identificador.toLowerCase(Locale.ROOT);
        Usuario usuario = usuarios.findByUsername(identificador)
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));
        if (!Boolean.TRUE.equals(usuario.getEnable())
                || !passwords.matches(request.password(), usuario.getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
        Long investigadorId = investigadores.findByUsuarioId(usuario.getId())
                .map(Investigador::getId).orElse(null);
        return new LoginResponse(jwt.generarToken(usuario, investigadorId), usuario.getUsername(),
                usuario.getRol().getNombre(), usuario.getId(), investigadorId);
    }

    /** Devuelve los datos necesarios para enviar el correo UNA VEZ confirmado el commit. */
    @Transactional
    public InvitacionPreparada prepararInvitacion(InvitacionRequest request, boolean reenviar) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        Usuario usuario = usuarios.buscarPorCorreoParaActualizar(email).orElse(null);
        if (usuario != null && Boolean.TRUE.equals(usuario.getEnable())) {
            throw new IllegalStateException("Ya existe una cuenta activa con ese correo");
        }
        if (usuario != null && !reenviar) {
            throw new IllegalStateException("La cuenta ya tiene una invitación. Usa Reenviar invitación");
        }
        if (usuario == null && reenviar) {
            throw new NoSuchElementException("No hay una invitación para ese correo");
        }
        boolean nueva = usuario == null;
        if (nueva) {
            usuario = new Usuario();
            usuario.setUsername(email);
            usuario.setPassword(passwords.encode(UUID.randomUUID().toString()));
            usuario.setEnable(false);
            usuario.setCreatedAt(LocalDateTime.now());
            usuario.setRol(roles.findByNombreIgnoreCase("INVESTIGADOR")
                    .orElseThrow(() -> new IllegalStateException("Falta configurar el rol INVESTIGADOR")));
        } else if (usuario.getRol() == null || !"INVESTIGADOR".equalsIgnoreCase(usuario.getRol().getNombre())) {
            throw new IllegalStateException("La cuenta no pertenece a un investigador");
        }
        String token = UUID.randomUUID().toString();
        usuario.setTokenInvitacion(token);
        usuario.setFechaExpiracionToken(LocalDateTime.now().plusHours(48));
        usuarios.saveAndFlush(usuario);

        if (nueva) {
            Investigador perfil = new Investigador();
            perfil.setUsuario(usuario);
            perfil.setNombre(request.nombre().trim());
            perfil.setCorreoInstitucional(email);
            investigadores.save(perfil);
        }
        return new InvitacionPreparada(email, request.nombre().trim(), token,
                usuario.getFechaExpiracionToken());
    }

    @Transactional(readOnly = true)
    public Usuario validarToken(String token) {
        return comprobarVigencia(usuarios.findByTokenInvitacion(token)
                .orElseThrow(() -> new IllegalArgumentException("El enlace de invitación no es válido o ya fue utilizado")));
    }

    private Usuario comprobarVigencia(Usuario usuario) {
        if (Boolean.TRUE.equals(usuario.getEnable()) || usuario.getFechaExpiracionToken() == null
                || !LocalDateTime.now().isBefore(usuario.getFechaExpiracionToken())) {
            throw new IllegalArgumentException("El enlace de invitación ha expirado o ya fue utilizado");
        }
        return usuario;
    }

    @Transactional
    public void configurarCuenta(ConfigurarCuentaRequest request) {
        Usuario usuario = comprobarVigencia(usuarios.buscarTokenParaActualizar(request.token())
                .orElseThrow(() -> new IllegalArgumentException("El enlace de invitación no es válido o ya fue utilizado")));
        usuario.setPassword(passwords.encode(request.password()));
        usuario.setEnable(true);
        usuario.setTokenInvitacion(null);
        usuario.setFechaExpiracionToken(null);
        usuarios.save(usuario);
    }

    public record InvitacionPreparada(String email, String nombre, String token, LocalDateTime venceEn) { }
}
