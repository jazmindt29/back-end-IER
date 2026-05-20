package com.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime; // Necesario para updated_at
import java.util.List;

@Entity
@Table(name = "laboratorios")
@Data
public class Laboratorio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "encargado_id")
    private Investigador encargado;

    // NUEVO: Relación con componentes
    @ManyToMany
    @JoinTable(
      name = "laboratorio_componente", // Nombre de la tabla intermedia
      joinColumns = @JoinColumn(name = "laboratorio_id"),
      inverseJoinColumns = @JoinColumn(name = "componente_id")
    )
    private List<Componente> componentes;
}