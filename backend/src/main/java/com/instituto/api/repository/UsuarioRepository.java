package com.instituto.api.repository;

import com.instituto.api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Útil para el login o para verificar si un username ya existe
    Optional<Usuario> findByUsername(String username);
}