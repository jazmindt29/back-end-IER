package com.instituto.api.service;

import com.instituto.api.entity.Investigador;
import com.instituto.api.entity.Laboratorio;
import com.instituto.api.repository.InvestigadorRepository;
import com.instituto.api.repository.LaboratorioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.instituto.api.repository.ReservaLaboratorioRepository;
import com.instituto.api.repository.ComponenteRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class LaboratorioService {
    private final ReservaLaboratorioRepository reservas;
    private final ComponenteRepository componentes;
    private final LaboratorioRepository repo;
    private final InvestigadorRepository investigadorRepo;
    private final AuditoriaService auditoriaService;

    public LaboratorioService(ReservaLaboratorioRepository reservas, ComponenteRepository componentes,
                              LaboratorioRepository repo, InvestigadorRepository investigadorRepo,
                              AuditoriaService auditoriaService) {
        this.reservas = reservas;
        this.componentes = componentes;
        this.repo = repo;
        this.investigadorRepo = investigadorRepo;
        this.auditoriaService = auditoriaService;
    }

    public List<Laboratorio> listar() { return repo.findAll(); }

    public Optional<Laboratorio> buscarPorId(Long id) { return repo.findById(id); }

    public Laboratorio guardar(Laboratorio lab) {
        boolean esNuevo = lab.getId() == null;
        Laboratorio guardado = repo.save(lab);
        auditoriaService.registrar(esNuevo ? "CREAR" : "ACTUALIZAR", "laboratorio", guardado.getId());
        return guardado;
    }

    public void eliminar(Long id) {
        if (!repo.existsById(id)) throw new NoSuchElementException("Laboratorio no encontrado");
        if (reservas.existsByLaboratorioId(id) || componentes.existsByLaboratorioId(id)) {
            throw new IllegalStateException("El laboratorio tiene reservas o componentes vinculados");
        }
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
            } else {
                lab.setEncargado(null);
            }
            Laboratorio guardado = repo.save(lab);
            auditoriaService.registrar("ACTUALIZAR", "laboratorio", id);
            return guardado;
        }).orElseThrow(() -> new NoSuchElementException("Laboratorio no encontrado"));
    }
}
