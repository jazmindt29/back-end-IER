package com.instituto.api.controller;

import com.instituto.api.entity.Investigador;
import com.instituto.api.security.JwtService;
import com.instituto.api.service.InvestigadorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/investigadores")
public class InvestigadorController {

    @Autowired
    private InvestigadorService service;

    // Público, con filtro opcional por área técnica ("Agua" o "Energía")
    @GetMapping
    public List<Investigador> listar(@RequestParam(required = false) String area) {
        return area != null && !area.isBlank()
                ? service.buscarPorArea(area)
                : service.listarTodos();
    }

    // Perfil del investigador autenticado (para su panel privado)
    @GetMapping("/mi-perfil")
    @PreAuthorize("hasRole('INVESTIGADOR')")
    public ResponseEntity<Investigador> miPerfil() {
        Long invId = JwtService.investigadorActualId();
        if (invId == null) {
            return ResponseEntity.notFound().build();
        }
        return service.buscarPorId(invId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Público
    @GetMapping("/{id}")
    public ResponseEntity<Investigador> buscar(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRADOR')")
    public Investigador crear(@Valid @RequestBody Investigador investigador) {
        return service.guardar(investigador);
    }

    // El admin puede editar cualquier perfil; el investigador solo el suyo
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRADOR','INVESTIGADOR')")
    public ResponseEntity<Investigador> actualizar(@PathVariable Long id, @Valid @RequestBody Investigador detalles) {
        if (!JwtService.esAdmin() && !id.equals(JwtService.investigadorActualId())) {
            throw new AccessDeniedException("Solo puedes modificar tu propio perfil");
        }
        return service.buscarPorId(id).map(investigador -> {
            investigador.setNombre(detalles.getNombre());
            investigador.setArea(detalles.getArea());
            investigador.setEspecialidad(detalles.getEspecialidad());
            investigador.setGrado(detalles.getGrado());
            investigador.setBio(detalles.getBio());
            investigador.setBiografia(detalles.getBiografia());
            investigador.setCorreoInstitucional(detalles.getCorreoInstitucional());
            investigador.setFotoUrl(detalles.getFotoUrl());
            return ResponseEntity.ok(service.guardar(investigador));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRADOR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
