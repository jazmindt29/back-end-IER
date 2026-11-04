package com.instituto.api.security;

import com.instituto.api.entity.Rol;
import com.instituto.api.entity.Usuario;
import com.instituto.api.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CuentaActivaValidatorTest {
    private final UsuarioRepository usuarios = mock(UsuarioRepository.class);
    private final CuentaActivaValidator validator = new CuentaActivaValidator(usuarios);
    private final Jwt token = Jwt.withTokenValue("test").header("alg", "HS256")
            .subject("admin").claim("userId", 1L).claim("rol", "ADMIN").build();

    @Test void rechazaCuentaEliminada() {
        when(usuarios.findById(1L)).thenReturn(Optional.empty());
        assertThat(validator.validate(token).hasErrors()).isTrue();
    }

    @Test void rechazaCuentaDeshabilitadaYCambioDeRol() {
        Usuario usuario = usuario(false, "ADMIN");
        when(usuarios.findById(1L)).thenReturn(Optional.of(usuario));
        assertThat(validator.validate(token).hasErrors()).isTrue();
        usuario.setEnable(true);
        usuario.getRol().setNombre("INVESTIGADOR");
        assertThat(validator.validate(token).hasErrors()).isTrue();
    }

    @Test void aceptaCuentaVigente() {
        when(usuarios.findById(1L)).thenReturn(Optional.of(usuario(true, "ADMIN")));
        assertThat(validator.validate(token).hasErrors()).isFalse();
    }

    private Usuario usuario(boolean enable, String rol) {
        Usuario usuario = new Usuario();
        usuario.setUsername("admin");
        usuario.setEnable(enable);
        Rol r = new Rol(); r.setNombre(rol); usuario.setRol(r);
        return usuario;
    }
}
