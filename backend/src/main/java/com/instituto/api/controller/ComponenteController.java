package com.instituto.api.controller;

import com.instituto.api.entity.Componente;
import com.instituto.api.repository.ComponenteRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/componentes")
public class ComponenteController {

    @Autowired
    private ComponenteRepository repository;

    // Público, con filtro opcional por laboratorio (catálogo de materiales)
    @GetMapping
    public List<Componente> listar(@RequestParam(required = false) Long laboratorioId) {
        return laboratorioId != null
                ? repository.findByLaboratorioId(laboratorioId)
                : repository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRADOR')")
    public Componente crear(@Valid @RequestBody Componente componente) {
        return repository.save(componente);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRADOR')")
    public ResponseEntity<Componente> actualizar(@PathVariable Long id, @Valid @RequestBody Componente detalles) {
        return repository.findById(id).map(comp -> {
            comp.setNombre(detalles.getNombre());
            comp.setDescripcion(detalles.getDescripcion());
            comp.setTipo(detalles.getTipo());
            comp.setLaboratorio(detalles.getLaboratorio());
            return ResponseEntity.ok(repository.save(comp));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRADOR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
