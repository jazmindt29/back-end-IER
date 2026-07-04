package com.instituto.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime; // Necesario para updated_at

@Entity
@Table(name = "laboratorios")
@Data
public class Laboratorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String descripcion; // Corregido: Asegúrate de que no tenga errores de dedo

    @Column(name = "imagen_url") // Agregado: para las fotos de los laboratorios
    private String imagenUrl;

    @Column(name = "updated_at") // Agregado: para la auditoría de tiempo
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "encargado_id") // Relación con la tabla investigadores
    private Investigador encargado;
}