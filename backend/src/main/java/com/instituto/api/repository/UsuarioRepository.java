package com.instituto.api.repository;

import com.instituto.api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select u from Usuario u where lower(u.username) = lower(:email)")
    Optional<Usuario> buscarPorCorreoParaActualizar(@org.springframework.data.repository.query.Param("email") String email);

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select u from Usuario u where u.tokenInvitacion = :token")
    Optional<Usuario> buscarTokenParaActualizar(@org.springframework.data.repository.query.Param("token") String token);

    // Útil para el login o para verificar si un username ya existe
    Optional<Usuario> findByUsername(String username);

    // Flujo de invitación: localizar la cuenta pendiente por su token
    Optional<Usuario> findByTokenInvitacion(String tokenInvitacion);

}
