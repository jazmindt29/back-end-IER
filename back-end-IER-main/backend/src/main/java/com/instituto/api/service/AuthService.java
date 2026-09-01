package com.instituto.api.service;

import com.instituto.api.dto.ConfigurarCuentaRequest;
import com.instituto.api.dto.InvitacionRequest;
import com.instituto.api.dto.LoginRequest;
import com.instituto.api.dto.LoginResponse;
import com.instituto.api.entity.Investigador;
import com.instituto.api.entity.Usuario;
import com.instituto.api.repository.InvestigadorRepository;
import com.instituto.api.repository.RolRepository;
import com.instituto.api.repository.UsuarioRepository;
import com.instituto.api.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class AuthService {

    // ponytail: el rol INVESTIGADOR usa el id 4, misma convención que UsuarioService
    private static final long ROL_INVESTIGADOR_ID = 4L;

    private final UsuarioRepository usuarioRepository;
    private final InvestigadorRepository investigadorRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MailService mailService;

    public AuthService(UsuarioRepository usuarioRepository,
                       InvestigadorRepository investigadorRepository,
                       RolRepository rolRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       MailService mailService) {
        this.usuarioRepository = usuarioRepository;
        this.investigadorRepository = investigadorRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.mailService = mailService;
    }

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));
        if (!Boolean.TRUE.equals(usuario.getEnable())
                || !passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
        Long investigadorId = investigadorRepository.findByUsuarioId(usuario.getId())
                .map(Investigador::getId).orElse(null);
        String token = jwtService.generarToken(usuario, investigadorId);
        String rol = usuario.getRol() != null ? usuario.getRol().getNombre() : null;
        return new LoginResponse(token, usuario.getUsername(), rol, usuario.getId(), investigadorId);
    }

    /**
     * Flujo de invitación: el admin registra nombre + correo institucional.
     * Se crea la cuenta inactiva (sin contraseña utilizable), su perfil de
     * investigador, y se envía por correo el enlace para establecer contraseña.
     */
    @Transactional
    public void invitarInvestigador(InvitacionRequest request) {
        if (usuarioRepository.findByUsername(request.email()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una cuenta con el correo " + request.email());
        }
        Usuario usuario = new Usuario();
        usuario.setUsername(request.email());
        // Placeholder aleatorio: la columna es NOT NULL y la cuenta inactiva no puede loguearse
        usuario.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        usuario.setEnable(false);
        usuario.setCreatedAt(LocalDateTime.now());
        usuario.setRol(rolRepository.findById(ROL_INVESTIGADOR_ID)
                .orElseThrow(() -> new NoSuchElementException("No existe el rol INVESTIGADOR (id " + ROL_INVESTIGADOR_ID + ")")));
        String token = UUID.randomUUID().toString();
        usuario.setTokenInvitacion(token);
        usuario.setFechaExpiracionToken(LocalDateTime.now().plusHours(48));
        usuario = usuarioRepository.save(usuario);

        Investigador perfil = new Investigador();
        perfil.setUsuario(usuario);
        perfil.setNombre(request.nombre());
        perfil.setCorreoInstitucional(request.email());
        investigadorRepository.save(perfil);

        mailService.enviarInvitacion(request.email(), request.nombre(), token);
    }

    /** Valida el token de invitación y devuelve la cuenta pendiente asociada. */
    public Usuario validarToken(String token) {
        Usuario usuario = usuarioRepository.findByTokenInvitacion(token)
                .orElseThrow(() -> new NoSuchElementException("Token de invitación inválido"));
        if (usuario.getFechaExpiracionToken() == null
                || usuario.getFechaExpiracionToken().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("El token de invitación ha expirado");
        }
        return usuario;
    }

    /** El investigador establece su propia contraseña (BCrypt) y la cuenta se activa. */
    @Transactional
    public void configurarCuenta(ConfigurarCuentaRequest request) {
        Usuario usuario = validarToken(request.token());
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setEnable(true);
        usuario.setTokenInvitacion(null);
        usuario.setFechaExpiracionToken(null);
        usuarioRepository.save(usuario);
    }
}
