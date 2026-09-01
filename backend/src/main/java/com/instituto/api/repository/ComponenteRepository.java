package com.instituto.api.repository;

import com.instituto.api.entity.Componente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComponenteRepository extends JpaRepository<Componente, Long> {
    // Materiales de un laboratorio específico (catálogo público)
    List<Componente> findByLaboratorioId(Long laboratorioId);
}