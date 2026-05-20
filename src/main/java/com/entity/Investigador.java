package com.instituto.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "investigadores")
@Data
public class Investigador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AreaInvestigacion area;
    private String especialidad;
    private String grado;
    private String bio;
    private String biografia;

    @Column(name = "foto_url")
    private String fotoUrl;

    @Column(name = "correo_institucional")
    private String correoInstitucional;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToMany(mappedBy = "investigadores")
    @com.fasterxml.jackson.annotation.JsonIgnore // <--- CRITICO
    private List<Proyecto> proyectos;
}