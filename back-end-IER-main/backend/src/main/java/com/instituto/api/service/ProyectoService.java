package com.instituto.api.service;

import com.instituto.api.entity.Proyecto;
import com.instituto.api.repository.ProyectoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ProyectoService {

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private AuditoriaService auditoriaService;

    // Listar todos
    public List<Proyecto> listarTodos() {
        return proyectoRepository.findAll();
    }

    // Listar solo destacados
    public List<Proyecto> listarDestacados() {
        return proyectoRepository.findByEsDestacadoTrue();
    }

    // Crear/Guardar
    public Proyecto guardar(Proyecto proyecto) {
        Proyecto guardado = proyectoRepository.save(proyecto);
        auditoriaService.registrar("CREAR", "proyecto", guardado.getId());
        return guardado;
    }

    public Proyecto actualizar(Long id, Proyecto detalles) {
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No existe el proyecto con ID: " + id));

        proyecto.setTitulo(detalles.getTitulo());
        proyecto.setDescripcion(detalles.getDescripcion());
        proyecto.setEstado(detalles.getEstado());
        proyecto.setEsDestacado(detalles.getEsDestacado());
        proyecto.setImagenUrl(detalles.getImagenUrl());
        if (detalles.getInvestigadores() != null) {
            proyecto.setInvestigadores(detalles.getInvestigadores());
        }

        Proyecto guardado = proyectoRepository.save(proyecto);
        auditoriaService.registrar("ACTUALIZAR", "proyecto", id);
        return guardado;
    }

    // Eliminar
    public void eliminar(Long id) {
        proyectoRepository.deleteById(id);
        auditoriaService.registrar("ELIMINAR", "proyecto", id);
    }
}
