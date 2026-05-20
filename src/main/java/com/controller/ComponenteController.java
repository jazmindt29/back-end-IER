package com.controller;

import com.entity.Componente;
import com.repository.ComponenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // Importante para @PostMapping y @RequestBody
import java.util.List;

@RestController
@RequestMapping("/api/componentes")
public class ComponenteController {

    @Autowired
    private ComponenteRepository repository;

    @GetMapping
    public List<Componente> listar() {
        return repository.findAll();
    }

    @PostMapping
     public Componente crear(@RequestBody Componente componente) { // <-- Asegúrate de que diga @RequestBody
    return repository.save(componente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Componente> actualizar(@PathVariable Long id, @RequestBody Componente detalles) {
        return repository.findById(id).map(comp -> {
            comp.setNombre(detalles.getNombre());
            comp.setDescripcion(detalles.getDescripcion());
            comp.setTipo(detalles.getTipo());
            return ResponseEntity.ok(repository.save(comp));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}