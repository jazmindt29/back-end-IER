package com.instituto.api.service;

import com.instituto.api.entity.Investigador;
import com.instituto.api.entity.Laboratorio;
import com.instituto.api.repository.InvestigadorRepository;
import com.instituto.api.repository.LaboratorioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class LaboratorioService {
    @Autowired
    private LaboratorioRepository repo;

    @Autowired
    private InvestigadorRepository investigadorRepo; // Para el encargado

    @Autowired
    private AuditoriaService auditoriaService;

    public List<Laboratorio> listar() { return repo.findAll(); }

    public Optional<Laboratorio> buscarPorId(Long id) { return repo.findById(id); }

    public Laboratorio guardar(Laboratorio lab) {
        boolean esNuevo = lab.getId() == null;
        Laboratorio guardado = repo.save(lab);
        auditoriaService.registrar(esNuevo ? "CREAR" : "ACTUALIZAR", "laboratorio", guardado.getId());
        return guardado;
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
        auditoriaService.registrar("ELIMINAR", "laboratorio", id);
    }

    public Laboratorio actualizar(Long id, Laboratorio detalles) {
        return repo.findById(id).map(lab -> {
            lab.setNombre(detalles.getNombre());
            lab.setDescripcion(detalles.getDescripcion());
            lab.setImagenUrl(detalles.getImagenUrl());

            if (detalles.getEncargado() != null && detalles.getEncargado().getId() != null) {
                Investigador encargado = investigadorRepo.findById(detalles.getEncargado().getId())
                    .orElseThrow(() -> new NoSuchElementException("Investigador no encontrado"));
                lab.setEncargado(encargado);
            }
            Laboratorio guardado = repo.save(lab);
            auditoriaService.registrar("ACTUALIZAR", "laboratorio", id);
            return guardado;
        }).orElseThrow(() -> new NoSuchElementException("Laboratorio no encontrado"));
    }
}
