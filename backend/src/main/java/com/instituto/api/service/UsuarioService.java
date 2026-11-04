package com.instituto.api.service;

import com.instituto.api.dto.UsuarioRequest;
import com.instituto.api.entity.Usuario;
import com.instituto.api.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {
    private final UsuarioRepository usuarios;
    private final RolRepository roles;
    private final InvestigadorRepository investigadores;
    private final AvisoRepository avisos;
    private final PasswordEncoder passwords;

    public UsuarioService(UsuarioRepository usuarios, RolRepository roles, InvestigadorRepository investigadores,
                          AvisoRepository avisos, PasswordEncoder passwords) {
        this.usuarios = usuarios;
        this.roles = roles;
        this.investigadores = investigadores;
        this.avisos = avisos;
        this.passwords = passwords;
    }

    public Usuario registrarUsuario(UsuarioRequest solicitud) {
        var rol = roles.findById(solicitud.rol().id())
                .orElseThrow(() -> new NoSuchElementException("Rol no encontrado"));
        if ("INVESTIGADOR".equalsIgnoreCase(rol.getNombre())) {
            throw new IllegalArgumentException("Para investigadores utiliza el flujo de invitaciones");
        }
        Usuario usuario = new Usuario();
        usuario.setUsername(solicitud.username().trim());
        usuario.setPassword(passwords.encode(solicitud.password()));
        usuario.setEnable(true);
        usuario.setCreatedAt(LocalDateTime.now());
        usuario.setRol(rol);
        return usuarios.save(usuario);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listar() { return usuarios.findAll(); }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorUsername(String username) { return usuarios.findByUsername(username); }

    public void eliminar(Long id) {
        if (!usuarios.existsById(id)) throw new NoSuchElementException("Usuario no encontrado");
        if (investigadores.findByUsuarioId(id).isPresent() || avisos.existsByAutorId(id)) {
            throw new IllegalStateException("La cuenta tiene un perfil o publicaciones vinculados");
        }
        if (id.equals(com.instituto.api.security.JwtService.usuarioActualId())) {
            throw new IllegalStateException("No puedes eliminar tu propia cuenta");
        }
        usuarios.deleteById(id);
    }
}
