package com.instituto.api.repository;

import com.instituto.api.entity.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.Optional;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {
    @Override
    @EntityGraph(attributePaths = "investigadores")
    List<Proyecto> findAll();

    @Override
    @EntityGraph(attributePaths = "investigadores")
    Optional<Proyecto> findById(Long id);

    boolean existsByInvestigadoresId(Long investigadorId);
    // Método personalizado: Spring entiende "FindBy" + "EsDestacado" + "True"
    @EntityGraph(attributePaths = "investigadores")
    List<Proyecto> findByEsDestacadoTrue();
}
