package com.instituto.api.security;

import com.instituto.api.entity.Rol;
import com.instituto.api.entity.Usuario;
import com.instituto.api.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class DataSeederTest {
    private final UsuarioRepository usuarios = mock(UsuarioRepository.class);
    private final RolRepository roles = mock(RolRepository.class);
    private final PasswordEncoder passwords = mock(PasswordEncoder.class);
    private final DataSeeder seeder = new DataSeeder(usuarios, roles, passwords);

    @Test void noCreaAdministradorSinConfiguracionExplicita() {
        configurar("", "");
        seeder.run();
        verify(usuarios, never()).save(any());
    }

    @Test void noAceptaPasswordInicialDebil() {
        configurar("admin-local", "corta");
        assertThatThrownBy(() -> seeder.run()).isInstanceOf(IllegalStateException.class);
        verify(usuarios, never()).save(any());
    }

    @Test void creaCuentaOpcionalConPasswordCifrada() {
        configurar("admin-local", "password-local-larga");
        Rol rol = new Rol(); rol.setNombre("ADMIN");
        when(roles.findByNombreIgnoreCase("ADMIN")).thenReturn(Optional.of(rol));
        when(passwords.encode("password-local-larga")).thenReturn("hash-test");
        seeder.run();
        var captura = org.mockito.ArgumentCaptor.forClass(Usuario.class);
        verify(usuarios).save(captura.capture());
        assertThat(captura.getValue().getPassword()).isEqualTo("hash-test");
        assertThat(captura.getValue().getUsername()).isEqualTo("admin-local");
    }

    private void configurar(String username, String password) {
        ReflectionTestUtils.setField(seeder, "adminUsername", username);
        ReflectionTestUtils.setField(seeder, "adminPassword", password);
    }
}
