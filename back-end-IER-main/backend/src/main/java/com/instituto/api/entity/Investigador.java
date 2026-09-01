package com.instituto.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "investigadores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Investigador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(name = "grado_academico")
    private String gradoAcademico;

    private String area;

    private String especialidad;

    private String correo;

    @Column(name = "foto_url")
    private String fotoUrl;

    @Column(name = "bio_corta")
    private String bioCorta;

    @Column(columnDefinition = "TEXT")
    private String biografia;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;
}