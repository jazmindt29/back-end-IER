package com.instituto.api.dto;

import com.instituto.api.entity.Investigador;
import com.instituto.api.entity.Laboratorio;
import com.instituto.api.entity.ReservaLaboratorio;
import java.time.LocalDate;
import java.time.LocalTime;

/** Datos mínimos necesarios para mostrar ocupación, sin exponer perfiles completos. */
public record ReservaResponse(Long id, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin,
                              LaboratorioResumen laboratorio, InvestigadorResumen investigador) {
    public static ReservaResponse desde(ReservaLaboratorio reserva) {
        Laboratorio lab = reserva.getLaboratorio();
        Investigador inv = reserva.getInvestigador();
        return new ReservaResponse(reserva.getId(), reserva.getFecha(), reserva.getHoraInicio(),
                reserva.getHoraFin(), new LaboratorioResumen(lab.getId(), lab.getNombre()),
                inv == null ? null : new InvestigadorResumen(inv.getId(), inv.getNombre()));
    }

    public record LaboratorioResumen(Long id, String nombre) { }
    public record InvestigadorResumen(Long id, String nombre) { }
}
