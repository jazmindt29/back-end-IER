package com.instituto.api.security;

import com.instituto.api.entity.Usuario;
import com.instituto.api.repository.RolRepository;
import com.instituto.api.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

/**
 * Cuenta inicial opcional, solo en desarrollo o durante bootstrap explícito.
 */
@Component
@Profile({"dev", "bootstrap"})
public class DataSeeder implements CommandLineRunner {

    @Value("${app.bootstrap.admin-username:}")
    private String adminUsername;

    @Value("${app.bootstrap.admin-password:}")
    private String adminPassword;

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UsuarioRepository usuarioRepository,
                      RolRepository rolRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Solo en BD vacía, para no resucitar cuentas borradas a propósito
        if (usuarioRepository.count() == 0 && !adminUsername.isBlank()) {
            if (adminPassword.length() < 12) {
                throw new IllegalStateException("APP_BOOTSTRAP_ADMIN_PASSWORD debe tener al menos 12 caracteres");
            }
            Usuario admin = new Usuario();
            admin.setUsername(adminUsername);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setEnable(true);
            admin.setCreatedAt(LocalDateTime.now());
            admin.setRol(rolRepository.findByNombreIgnoreCase("ADMIN")
                    .or(() -> rolRepository.findByNombreIgnoreCase("ADMINISTRADOR")).orElseThrow());
            usuarioRepository.save(admin);
            log.info("Cuenta inicial de desarrollo creada; no se registran credenciales");
        }
    }
}
