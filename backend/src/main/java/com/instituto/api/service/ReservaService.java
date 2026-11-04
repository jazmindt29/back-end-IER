package com.instituto.api.service;

import com.instituto.api.entity.Investigador;
import com.instituto.api.entity.ReservaLaboratorio;
import com.instituto.api.repository.InvestigadorRepository;
import com.instituto.api.repository.ReservaLaboratorioRepository;
import com.instituto.api.security.JwtService;
import com.instituto.api.dto.ReservaRequest;
import com.instituto.api.repository.LaboratorioRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.time.LocalDateTime;

@Service
public class ReservaService {

    private final ReservaLaboratorioRepository repo;
    private final InvestigadorRepository investigadorRepository;
    private final LaboratorioRepository laboratorios;

    public ReservaService(ReservaLaboratorioRepository repo, InvestigadorRepository investigadorRepository,
                          LaboratorioRepository laboratorios) {
        this.repo = repo;
        this.investigadorRepository = investigadorRepository;
        this.laboratorios = laboratorios;
    }

    public List<ReservaLaboratorio> listar() {
        return repo.findAll();
    }

    public List<ReservaLaboratorio> listarPorInvestigador(Long investigadorId) {
        return repo.findByInvestigadorIdOrderByFechaDesc(investigadorId);
    }

    @Transactional
    public ReservaLaboratorio crear(ReservaRequest solicitud) {
        if (solicitud.id() != null) {
            throw new IllegalArgumentException("No se permite indicar un ID al crear");
        }
        ReservaLaboratorio reserva = new ReservaLaboratorio();
        reserva.setFecha(solicitud.fecha());
        reserva.setHoraInicio(solicitud.horaInicio());
        reserva.setHoraFin(solicitud.horaFin());
        // El bloqueo del laboratorio serializa también las reservas de un día aún vacío.
        reserva.setLaboratorio(laboratorios.buscarParaReservar(solicitud.laboratorio().id())
                .orElseThrow(() -> new NoSuchElementException("Laboratorio no encontrado")));
        if (!reserva.getHoraFin().isAfter(reserva.getHoraInicio())) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio");
        }
        if (!LocalDateTime.of(reserva.getFecha(), reserva.getHoraInicio()).isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("No puedes reservar un horario que ya comenzó");
        }
        // Un investigador solo agenda a su propio nombre; el admin puede agendar para cualquiera
        if (!JwtService.esAdmin()) {
            Long invId = JwtService.investigadorActualId();
            if (invId == null) {
                throw new AccessDeniedException("Tu cuenta no tiene perfil de investigador");
            }
            Investigador propio = investigadorRepository.findById(invId)
                    .orElseThrow(() -> new NoSuchElementException("No existe el investigador " + invId));
            reserva.setInvestigador(propio);
        } else {
            if (solicitud.investigador() == null) {
                throw new IllegalArgumentException("Debes indicar el investigador que reserva");
            }
            reserva.setInvestigador(investigadorRepository.findById(solicitud.investigador().id())
                    .orElseThrow(() -> new NoSuchElementException("Investigador no encontrado")));
        }
        validarColision(reserva);
        return repo.save(reserva);
    }

    // No se permiten dos reservas del mismo laboratorio, el mismo día,
    // en rangos de tiempo que se traslapen
    private void validarColision(ReservaLaboratorio nueva) {
        List<ReservaLaboratorio> delDia =
                repo.findByLaboratorioIdAndFecha(nueva.getLaboratorio().getId(), nueva.getFecha());
        boolean choca = delDia.stream()
                .anyMatch(r -> r.getHoraInicio().isBefore(nueva.getHoraFin())
                        && nueva.getHoraInicio().isBefore(r.getHoraFin()));
        if (choca) {
            throw new IllegalStateException("El laboratorio ya está reservado en ese horario");
        }
    }

    @Transactional
    public void eliminar(Long id) {
        ReservaLaboratorio reserva = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No existe la reserva " + id));
        // El investigador solo puede cancelar sus propias reservas
        if (!JwtService.esAdmin()) {
            Long invId = JwtService.investigadorActualId();
            if (reserva.getInvestigador() == null || invId == null
                    || !invId.equals(reserva.getInvestigador().getId())) {
                throw new AccessDeniedException("Solo puedes cancelar tus propias reservas");
            }
        }
        repo.delete(reserva);
    }
}
