package com.instituto.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "componentes")
@Data
public class Componente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nombre;
    private String descripcion;
    private String tipo;

    // Cada material/componente pertenece obligatoriamente a un laboratorio.
    // @NotNull valida la entrada; la columna queda nullable en BD para no romper filas previas
    @NotNull
    @ManyToOne
    @JoinColumn(name = "laboratorio_id")
    private Laboratorio laboratorio;
}
