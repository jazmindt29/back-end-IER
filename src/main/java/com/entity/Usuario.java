package com.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;



@Entity
@Table(name = "users")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    // Cambiado a 'enable' para coincidir exactamente con pgAdmin
    private Boolean enable; 

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Relación con el Rol (usando la columna que conecta con la tabla roles)
    @ManyToOne
    @JoinColumn(name = "role_id") 
    private Rol rol;
}