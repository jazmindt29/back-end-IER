package com.instituto.api.security;

import com.instituto.api.entity.Usuario;
import com.instituto.api.repository.RolRepository;
import com.instituto.api.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Siembra idempotente para entornos recién clonados: garantiza los roles
 * (ids 1=ADMIN y 4=INVESTIGADOR, convención fija usada por AuthService) y,
 * solo si no existe ningún usuario, crea admin/admin123.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final JdbcTemplate jdbc;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(JdbcTemplate jdbc, UsuarioRepository usuarioRepository,
                      RolRepository rolRepository, PasswordEncoder passwordEncoder) {
        this.jdbc = jdbc;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Ids explícitos porque el código depende de ellos; ON CONFLICT lo hace re-ejecutable
        jdbc.update("INSERT INTO roles (id, name) VALUES (1,'ADMIN'), (4,'INVESTIGADOR') ON CONFLICT DO NOTHING");

        // Solo en BD vacía, para no resucitar cuentas borradas a propósito
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEnable(true);
            admin.setCreatedAt(LocalDateTime.now());
            admin.setRol(rolRepository.findById(1L).orElseThrow());
            usuarioRepository.save(admin);
            log.info("BD sin usuarios: creado admin/admin123 (cambiar en producción)");
        }
    }
}
