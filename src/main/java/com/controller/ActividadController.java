package com.controller;

import com.entity.Actividad;
import com.repository.ActividadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/actividades")
public class ActividadController {

    @Autowired
    private ActividadRepository repository;

    @GetMapping
    public List<Actividad> listar() {
        return repository.findAll();
    }

    @PostMapping
    public Actividad crear(@RequestBody Actividad actividad) {
        return repository.save(actividad);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Actividad> actualizar(@PathVariable Long id, @RequestBody Actividad detalles) {
        return repository.findById(id).map(act -> {
            act.setTitulo(detalles.getTitulo());
            act.setFecha(detalles.getFecha());
            act.setDescripcion(detalles.getDescripcion());
            return ResponseEntity.ok(repository.save(act));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}