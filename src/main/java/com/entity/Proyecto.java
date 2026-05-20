package com.instituto.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "proyectos")
@Data
public class Proyecto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descripcion;
    private String estado; 
    
    // Usamos es_destacado para que los métodos del repository 
    // como findByEsDestacadoTrue coincidan con el nombre del campo
    @Column(name = "es_destacado")
    private Boolean esDestacado;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToMany
    @JoinTable(
        name = "proyecto_investigadores",
        joinColumns = @JoinColumn(name = "proyecto_id"),
        // AJUSTE: He puesto el guion bajo final para que coincida con tu pgAdmin
        inverseJoinColumns = @JoinColumn(name = "investigador_id") 
    )
    private List<Investigador> investigadores;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}