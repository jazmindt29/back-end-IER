package com.controller;

import com.entity.Laboratorio;
import com.service.LaboratorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/laboratorios")
public class LaboratorioController {

    @Autowired
    private LaboratorioService service;

    @GetMapping
    public List<Laboratorio> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Laboratorio> buscar(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Laboratorio crear(@RequestBody Laboratorio lab) {
        return service.guardar(lab);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Laboratorio> actualizar(@PathVariable Long id, @RequestBody Laboratorio detalles) {
        return service.buscarPorId(id).map(lab -> {
            lab.setNombre(detalles.getNombre());
            lab.setDescripcion(detalles.getDescripcion());
            lab.setImagenUrl(detalles.getImagenUrl());
            lab.setEncargado(detalles.getEncargado());
            return ResponseEntity.ok(service.guardar(lab));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/{id}/componentes/{componenteId}")
    public ResponseEntity<Laboratorio> asignarComponente(
            @PathVariable Long id, 
            @PathVariable Long componenteId) {
        try {
            Laboratorio actualizado = service.asignarComponente(id, componenteId);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
            }
}