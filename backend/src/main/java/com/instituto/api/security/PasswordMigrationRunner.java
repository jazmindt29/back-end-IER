package com.instituto.api.security;

import com.instituto.api.entity.Usuario;
import com.instituto.api.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Migración única e idempotente: cifra con BCrypt cualquier contraseña que
 * siga almacenada en texto plano (los hashes BCrypt empiezan con "$2").
 * Se ejecuta en cada arranque pero solo re-escribe las que aún son planas.
 */
@Component
public class PasswordMigrationRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PasswordMigrationRunner.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordMigrationRunner(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        List<Usuario> pendientes = usuarioRepository.findAll().stream()
                .filter(u -> u.getPassword() != null && !u.getPassword().startsWith("$2"))
                .toList();
        for (Usuario u : pendientes) {
            u.setPassword(passwordEncoder.encode(u.getPassword()));
            usuarioRepository.save(u);
        }
        if (!pendientes.isEmpty()) {
            log.info("Se cifraron {} contraseña(s) que estaban en texto plano", pendientes.size());
        }
    }
}
