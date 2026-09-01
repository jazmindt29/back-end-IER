package com.instituto.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "reservas_laboratorio")
@Data
public class ReservaLaboratorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDate fecha;

    @NotNull
    @Column(name = "hora_inicio")
    private LocalTime horaInicio;

    @NotNull
    @Column(name = "hora_fin")
    private LocalTime horaFin;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "laboratorio_id")
    private Laboratorio laboratorio;

    // Sin @NotNull: cuando reserva un investigador, el servidor lo asigna desde
    // su sesión (JWT); solo el administrador puede indicarlo explícitamente
    @ManyToOne
    @JoinColumn(name = "investigador_id")
    private Investigador investigador;
}
