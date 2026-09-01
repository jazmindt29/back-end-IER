package com.instituto.api.repository;

import com.instituto.api.entity.Investigador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvestigadorRepository extends JpaRepository<Investigador, Long> {
    
    // Filtro público por área técnica ("Agua" / "Energía")
    List<Investigador> findByArea(String area);

    // Perfil vinculado a una cuenta de usuario (para los claims del JWT en el login)
  

    Optional<Investigador> findByUsuarioUsername(String username);
    Optional<Investigador> findByUsuarioId(Long usuarioId);
}
