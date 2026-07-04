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

    // Listar todos (El que ya tienes)
    @GetMapping
    public List<Proyecto> listar() {
        return proyectoService.listarTodos();
    }

    // CREAR (POST) - Necesario para registrar nuevos proyectos
    @PostMapping
    public ResponseEntity<Proyecto> crear(@RequestBody Proyecto proyecto) {
        return new ResponseEntity<>(proyectoService.guardar(proyecto), HttpStatus.CREATED);
    }

    // ACTUALIZAR (PUT) - Necesario para cambiar estado o descripción
    @PutMapping("/{id}")
    public ResponseEntity<Proyecto> actualizar(@PathVariable Long id, @RequestBody Proyecto detalles) {
        return ResponseEntity.ok(proyectoService.actualizar(id, detalles));
    }

    // ELIMINAR (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        proyectoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}