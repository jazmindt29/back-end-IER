package com.instituto.api.controller;

import com.instituto.api.entity.Aviso;
import com.instituto.api.repository.AvisoRepository;
import com.instituto.api.repository.UsuarioRepository;
import com.instituto.api.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/avisos")
public class AvisoController {

    @Autowired
    private AvisoRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Público: los estudiantes ven los avisos sin autenticarse
    @GetMapping
    public List<Aviso> listar() {
        return repository.findAllByOrderByFechaPublicacionDesc();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRADOR')")
    public ResponseEntity<Aviso> crear(@Valid @RequestBody Aviso aviso) {
        // El autor es siempre el administrador autenticado, no lo decide el cliente
        Long adminId = JwtService.usuarioActualId();
        if (adminId != null) {
            aviso.setAutor(usuarioRepository.getReferenceById(adminId));
        }
        return new ResponseEntity<>(repository.save(aviso), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRADOR')")
    public ResponseEntity<Aviso> actualizar(@PathVariable Long id, @Valid @RequestBody Aviso detalles) {
        return repository.findById(id).map(aviso -> {
            aviso.setTitulo(detalles.getTitulo());
            aviso.setContenido(detalles.getContenido());
            aviso.setImagenUrl(detalles.getImagenUrl());
            return ResponseEntity.ok(repository.save(aviso));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRADOR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
