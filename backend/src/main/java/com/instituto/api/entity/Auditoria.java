package com.instituto.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
@Data
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private String bio;

    private Boolean destacado;

    // ID del usuario autenticado que realizó la acción (desde SecurityContext)
    @Column(name = "updated_by")
    private Long updatedBy;

    // Qué se hizo (CREAR / ACTUALIZAR / ELIMINAR) y sobre qué registro
    private String accion;

    private String entidad;

    @Column(name = "entidad_id")
    private Long entidadId;
}
