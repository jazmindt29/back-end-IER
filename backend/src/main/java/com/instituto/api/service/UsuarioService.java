package com.instituto.api.service;

import com.instituto.api.entity.Investigador;
import com.instituto.api.entity.Usuario;
import com.instituto.api.repository.InvestigadorRepository;
import com.instituto.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private InvestigadorRepository investigadorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    @Transactional
    public Usuario registrarUsuario(Usuario usuario) {
        if (usuario.getPassword() != null && !usuario.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }

        if (usuario.getCreatedAt() == null) {
            usuario.setCreatedAt(LocalDateTime.now());
        }

        // Generar token UUID limpio
        String token = UUID.randomUUID().toString();
        usuario.setTokenInvitacion(token);
        usuario.setFechaExpiracionToken(LocalDateTime.now().plusHours(48));

        Usuario nuevoUsuario = usuarioRepository.save(usuario);

        // Si es rol Investigador (ID 4), se crea su perfil automáticamente
        if (nuevoUsuario.getRol() != null && nuevoUsuario.getRol().getId() == 4) {
            Investigador perfil = new Investigador();
            perfil.setUsuario(nuevoUsuario);
            perfil.setNombre(nuevoUsuario.getUsername());
            investigadorRepository.save(perfil);
        }

        try {
            mailService.enviarInvitacion(nuevoUsuario.getUsername(), nuevoUsuario.getUsername(), token);
        } catch (Exception e) {
            System.err.println("Error al enviar correo: " + e.getMessage());
        }

        return nuevoUsuario;
    }

    public boolean validarTokenInvitacion(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        return usuarioRepository.findByTokenInvitacion(token)
                .map(usuario -> usuario.getFechaExpiracionToken() != null 
                        && LocalDateTime.now().isBefore(usuario.getFechaExpiracionToken()))
                .orElse(false);
    }

    @Transactional
    public void activarCuenta(String token, String nuevaPassword) {
        Usuario usuario = usuarioRepository.findByTokenInvitacion(token)
                .orElseThrow(() -> new RuntimeException("El token proporcionado no existe."));

        if (usuario.getFechaExpiracionToken() == null || LocalDateTime.now().isAfter(usuario.getFechaExpiracionToken())) {
            throw new RuntimeException("El enlace de invitación ha expirado.");
        }

        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuario.setEnable(true);
        usuario.setTokenInvitacion(null);
        usuario.setFechaExpiracionToken(null);

        usuarioRepository.save(usuario);
    }

    public List<Usuario> listar() { 
        return usuarioRepository.findAll(); 
    }

    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    public void eliminar(Long id) { 
        usuarioRepository.deleteById(id); 
    }
}