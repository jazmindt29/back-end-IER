package com.instituto.api.controller;

import com.instituto.api.entity.ReservaLaboratorio;
import com.instituto.api.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaService service;

    // Público: los estudiantes consultan qué horarios están ocupados
    @GetMapping
    public List<ReservaLaboratorio> listar(@RequestParam(required = false) Long investigadorId) {
        return investigadorId != null
                ? service.listarPorInvestigador(investigadorId)
                : service.listar();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRADOR','INVESTIGADOR')")
    public ResponseEntity<ReservaLaboratorio> crear(@Valid @RequestBody ReservaLaboratorio reserva) {
        return new ResponseEntity<>(service.crear(reserva), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRADOR','INVESTIGADOR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
