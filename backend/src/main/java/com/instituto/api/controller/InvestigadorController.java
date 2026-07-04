package com.instituto.api.controller;

import com.instituto.api.entity.Investigador;
import com.instituto.api.service.InvestigadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/investigadores")
public class InvestigadorController {

    @Autowired
    private InvestigadorService service;

    @GetMapping
    public List<Investigador> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Investigador> buscar(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Investigador crear(@RequestBody Investigador investigador) {
        return service.guardar(investigador);
    }

   @PutMapping("/{id}")
public ResponseEntity<Investigador> actualizar(@PathVariable Long id, @RequestBody Investigador detalles) {
    return service.buscarPorId(id).map(investigador -> {
        investigador.setNombre(detalles.getNombre());
        investigador.setArea(detalles.getArea()); // Usamos getArea() y setArea()
        investigador.setGrado(detalles.getGrado());
        investigador.setBio(detalles.getBio());
        investigador.setCorreoInstitucional(detalles.getCorreoInstitucional());
        investigador.setFotoUrl(detalles.getFotoUrl());
        
        return ResponseEntity.ok(service.guardar(investigador));
    }).orElse(ResponseEntity.notFound().build());
}
 // Dentro de InvestigadorController.java

@DeleteMapping("/{id}")
public ResponseEntity<Void> eliminar(@PathVariable Long id) {
    service.eliminar(id); // O el nombre de método que uses en tu service
    return ResponseEntity.noContent().build();
}
}