package com.instituto.api.repository;

import com.instituto.api.entity.Laboratorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface LaboratorioRepository extends JpaRepository<Laboratorio, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from Laboratorio l where l.id = :id")
    Optional<Laboratorio> buscarParaReservar(@Param("id") Long id);

    boolean existsByEncargadoId(Long investigadorId);
}
