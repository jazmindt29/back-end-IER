package com.entity;

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

    private String bio; // Coincide con 'bio' (text) en pgAdmin

    private Boolean destacado; // Coincide con 'destacado' (boolean) en pgAdmin

    @Column(name = "updated_by")
    private Long updatedBy; 

    
}