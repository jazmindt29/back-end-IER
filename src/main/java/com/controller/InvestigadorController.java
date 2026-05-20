package com.instituto.api.controller;

import com.instituto.api.dto.InvestigadorDTO;
import com.instituto.api.entity.AreaInvestigacion;
import com.instituto.api.entity.Investigador;
import com.instituto.api.service.InvestigadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Cambia el @CrossOrigin para que acepte TODO mientras pruebas
@RestController
@RequestMapping("/api/investigadores")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class InvestigadorController {

    @Autowired
    private InvestigadorService service;

    // CAMBIO 1: Usa DTO también para crear
    @PostMapping
    public ResponseEntity<Investigador> crear(@RequestBody InvestigadorDTO dto) {
        // El service debe tener un método que acepte DTO para crear
        return ResponseEntity.ok(service.crearDesdeDTO(dto)); 
    }

    @GetMapping
    public List<Investigador> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Investigador> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @GetMapping("/area/{area}")
    public List<Investigador> buscarPorArea(@PathVariable String area) {
        AreaInvestigacion areaEnum = AreaInvestigacion.valueOf(area.toUpperCase());
        return service.buscarPorArea(areaEnum);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Investigador> actualizar(@PathVariable Long id, @RequestBody InvestigadorDTO dto) {
        // Aquí ya usabas DTO, esto está bien encaminado
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}