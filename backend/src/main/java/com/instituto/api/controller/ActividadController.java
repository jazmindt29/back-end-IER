package com.instituto.api.controller;

import com.instituto.api.entity.Actividad;
import com.instituto.api.repository.ActividadRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/actividades")
public class ActividadController {

    @Autowired
    private ActividadRepository repository;

    // Público
    @GetMapping
    public List<Actividad> listar() {
        return repository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRADOR')")
    public Actividad crear(@Valid @RequestBody Actividad actividad) {
        return repository.save(actividad);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRADOR')")
    public ResponseEntity<Actividad> actualizar(@PathVariable Long id, @Valid @RequestBody Actividad detalles) {
        return repository.findById(id).map(act -> {
            act.setTitulo(detalles.getTitulo());
            act.setFecha(detalles.getFecha());
            act.setDescripcion(detalles.getDescripcion());
            return ResponseEntity.ok(repository.save(act));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRADOR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
