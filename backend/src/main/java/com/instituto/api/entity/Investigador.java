package com.instituto.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "investigadores")
@Data
public class Investigador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String area;
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
}