package com.instituto.api.controller;

import com.instituto.api.entity.Investigador;
import com.instituto.api.service.InvestigadorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/investigadores")
public class InvestigadorController {

    private final InvestigadorService investigadorService;

    public InvestigadorController(InvestigadorService investigadorService) {
        this.investigadorService = investigadorService;
    }

    @GetMapping
    public ResponseEntity<List<Investigador>> listarTodos() {
        return ResponseEntity.ok(investigadorService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Investigador> buscarPorId(@PathVariable Long id) {
        return investigadorService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/mi-perfil")
    public ResponseEntity<Investigador> obtenerMiPerfil(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(investigadorService.obtenerMiPerfil(username));
    }

    @PutMapping("/mi-perfil")
    @PreAuthorize("hasAnyRole('INVESTIGADOR', 'ADMIN')")
    public ResponseEntity<Investigador> actualizarMiPerfil(
            Authentication authentication,
            @RequestBody Investigador datos) {
        String username = authentication.getName();
        Investigador guardado = investigadorService.guardarOActualizarMiPerfil(username, datos);
        return ResponseEntity.ok(guardado);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Investigador> crear(@RequestBody Investigador investigador) {
        return ResponseEntity.ok(investigadorService.guardar(investigador));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        investigadorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}