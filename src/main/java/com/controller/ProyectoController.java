package com.instituto.api.controller;

import com.instituto.api.entity.Proyecto;
import com.instituto.api.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proyectos")
@CrossOrigin("*") // Para que tu frontend pueda conectarse después
public class ProyectoController {
    @Autowired
    private ProyectoService proyectoService;

    // 1. LISTAR TODOS
    @GetMapping
    public List<Proyecto> listar() {
        return proyectoService.listarTodos();
    }

    // 2. DESTACADOS 
    @GetMapping("/destacados")
    public List<Proyecto> listarDestacados() {
        return proyectoService.listarDestacados();
    }

    // 3. OBTENER POR ID 
    @GetMapping("/{id}")
    public ResponseEntity<Proyecto> obtenerPorId(@PathVariable Long id) {
        return proyectoService.obtenerPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping
    public ResponseEntity<Proyecto> crear(@RequestBody Proyecto proyecto) {
        return new ResponseEntity<>(proyectoService.guardar(proyecto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Proyecto> actualizar(@PathVariable Long id, @RequestBody Proyecto detalles) {
        return ResponseEntity.ok(proyectoService.actualizar(id, detalles));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        proyectoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/investigadores/{investigadorId}")
    public ResponseEntity<Proyecto> asignarInvestigador(
        @PathVariable Long id, 
        @PathVariable Long investigadorId) {
        return ResponseEntity.ok(proyectoService.asignarInvestigador(id, investigadorId));
    }
}