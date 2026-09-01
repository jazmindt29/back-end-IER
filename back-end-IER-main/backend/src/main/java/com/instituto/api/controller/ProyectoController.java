package com.instituto.api.controller;

import com.instituto.api.entity.Proyecto;
import com.instituto.api.service.ProyectoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proyectos")
public class ProyectoController {

    @Autowired
    private ProyectoService proyectoService;

    // Público
    @GetMapping
    public List<Proyecto> listar() {
        return proyectoService.listarTodos();
    }

    // Público: proyectos destacados para el home
    @GetMapping("/destacados")
    public List<Proyecto> destacados() {
        return proyectoService.listarDestacados();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRADOR')")
    public ResponseEntity<Proyecto> crear(@Valid @RequestBody Proyecto proyecto) {
        return new ResponseEntity<>(proyectoService.guardar(proyecto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRADOR')")
    public ResponseEntity<Proyecto> actualizar(@PathVariable Long id, @Valid @RequestBody Proyecto detalles) {
        return ResponseEntity.ok(proyectoService.actualizar(id, detalles));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRADOR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        proyectoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
