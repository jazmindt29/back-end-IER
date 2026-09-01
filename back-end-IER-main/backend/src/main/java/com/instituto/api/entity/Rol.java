package com.instituto.api.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "roles")
@Data
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // La columna en BD se llama "name"
    @Column(name = "name", nullable = false, unique = true)
    private String nombre;
}