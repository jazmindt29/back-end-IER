package com.instituto.api.controller;

import com.instituto.api.entity.Auditoria;
import com.instituto.api.service.AuditoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {

    @Autowired
    private AuditoriaService service;

    // Solo el administrador puede consultar el historial de cambios
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRADOR')")
    public List<Auditoria> listar() {
        return service.listarTodas();
    }
}
