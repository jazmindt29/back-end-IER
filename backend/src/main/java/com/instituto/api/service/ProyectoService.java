package com.instituto.api.service;

import com.instituto.api.entity.Proyecto;
import com.instituto.api.repository.ProyectoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProyectoService {

    @Autowired
    private ProyectoRepository proyectoRepository;

    // Listar todos
    public List<Proyecto> listarTodos() {
        return proyectoRepository.findAll();
    }

    // Listar solo destacados (Asegúrate de tener este método en el Repository)
    public List<Proyecto> listarDestacados() {
        return proyectoRepository.findByEsDestacadoTrue();
    }

    // Obtener por ID
    public Optional<Proyecto> obtenerPorId(Long id) {
        return proyectoRepository.findById(id);
    }

    // Crear/Guardar
    public Proyecto guardar(Proyecto proyecto) {
        return proyectoRepository.save(proyecto);
    }

    // ProyectoService.java
public Proyecto actualizar(Long id, Proyecto detalles) {
    Proyecto proyecto = proyectoRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("No existe el proyecto con ID: " + id));
    
    proyecto.setTitulo(detalles.getTitulo());
    proyecto.setDescripcion(detalles.getDescripcion());
    proyecto.setEstado(detalles.getEstado());
    
    // Usa estos nombres exactos:
    proyecto.setEsDestacado(detalles.getEsDestacado()); 
    proyecto.setImagenUrl(detalles.getImagenUrl());
    
    return proyectoRepository.save(proyecto);
}

    // Eliminar
    public void eliminar(Long id) {
        proyectoRepository.deleteById(id);
    }
}