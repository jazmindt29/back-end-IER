package com.instituto.api.repository;

import com.instituto.api.entity.ReservaLaboratorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaLaboratorioRepository extends JpaRepository<ReservaLaboratorio, Long> {
    // Reservas existentes de un laboratorio en una fecha, para detectar traslapes
    List<ReservaLaboratorio> findByLaboratorioIdAndFecha(Long laboratorioId, LocalDate fecha);

    List<ReservaLaboratorio> findByInvestigadorIdOrderByFechaDesc(Long investigadorId);
}
