package com.instituto.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
@Table(name = "componentes")
@Data
public class Componente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private String tipo;

    @ManyToMany(mappedBy = "componentes")
    @JsonIgnore 
    private List<Laboratorio> laboratorios;
}