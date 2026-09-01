package com.instituto.api.service;

import com.instituto.api.entity.Investigador;
import com.instituto.api.entity.ReservaLaboratorio;
import com.instituto.api.repository.InvestigadorRepository;
import com.instituto.api.repository.ReservaLaboratorioRepository;
import com.instituto.api.security.JwtService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ReservaService {

    private final ReservaLaboratorioRepository repo;
    private final InvestigadorRepository investigadorRepository;

    public ReservaService(ReservaLaboratorioRepository repo, InvestigadorRepository investigadorRepository) {
        this.repo = repo;
        this.investigadorRepository = investigadorRepository;
    }

    public List<ReservaLaboratorio> listar() {
        return repo.findAll();
    }

    public List<ReservaLaboratorio> listarPorInvestigador(Long investigadorId) {
        return repo.findByInvestigadorIdOrderByFechaDesc(investigadorId);
    }

    public ReservaLaboratorio crear(ReservaLaboratorio reserva) {
        if (!reserva.getHoraFin().isAfter(reserva.getHoraInicio())) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio");
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
        } else if (reserva.getInvestigador() == null || reserva.getInvestigador().getId() == null) {
            throw new IllegalArgumentException("Debes indicar el investigador que reserva");
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
                .filter(r -> !r.getId().equals(nueva.getId()))
                .anyMatch(r -> r.getHoraInicio().isBefore(nueva.getHoraFin())
                        && nueva.getHoraInicio().isBefore(r.getHoraFin()));
        if (choca) {
            throw new IllegalStateException("El laboratorio ya está reservado en ese horario");
        }
    }

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
