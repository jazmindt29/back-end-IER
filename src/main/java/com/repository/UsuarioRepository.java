package com.instituto.api.repository;

import com.instituto.api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // CAMBIO: Borra findByEmail y usa solo findByUsername
    Optional<Usuario> findByUsername(String username);
}