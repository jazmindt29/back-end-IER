package com.instituto.api.service;

import com.instituto.api.entity.Proyecto;
import com.instituto.api.entity.Investigador;
import com.instituto.api.dto.ProyectoRequest;
import com.instituto.api.repository.InvestigadorRepository;
import com.instituto.api.repository.ProyectoRepository;
import com.instituto.api.security.JwtService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProyectoService {
    private final ProyectoRepository proyectoRepository;
    private final InvestigadorRepository investigadores;
    private final AuditoriaService auditoriaService;

    public ProyectoService(ProyectoRepository proyectoRepository, InvestigadorRepository investigadores,
                           AuditoriaService auditoriaService) {
        this.proyectoRepository = proyectoRepository;
        this.investigadores = investigadores;
        this.auditoriaService = auditoriaService;
    }

    // Listar todos
    public List<Proyecto> listarTodos() {
        return proyectoRepository.findAll();
    }

    // Listar solo destacados
    public List<Proyecto> listarDestacados() {
        return proyectoRepository.findByEsDestacadoTrue();
    }

    // Crear/Guardar
    public Proyecto guardar(ProyectoRequest solicitud) {
        Proyecto proyecto = new Proyecto();
        aplicarCambios(proyecto, solicitud);
        proyecto.setCreatedBy(JwtService.usuarioActualId());
        Proyecto guardado = proyectoRepository.save(proyecto);
        auditoriaService.registrar("CREAR", "proyecto", guardado.getId());
        return guardado;
    }

    public Proyecto actualizar(Long id, ProyectoRequest detalles) {
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No existe el proyecto con ID: " + id));

        aplicarCambios(proyecto, detalles);

        Proyecto guardado = proyectoRepository.save(proyecto);
        auditoriaService.registrar("ACTUALIZAR", "proyecto", id);
        return guardado;
    }

    private void aplicarCambios(Proyecto proyecto, ProyectoRequest solicitud) {
        proyecto.setTitulo(solicitud.titulo().trim());
        proyecto.setDescripcion(solicitud.descripcion());
        proyecto.setEstado(solicitud.estado());
        proyecto.setEsDestacado(solicitud.esDestacado());
        proyecto.setImagenUrl(solicitud.imagenUrl());
        var ids = solicitud.investigadorIds();
        if (ids.size() != new HashSet<>(ids).size()) {
            throw new IllegalArgumentException("No se puede asociar dos veces al mismo investigador");
        }
        Map<Long, Investigador> encontrados = investigadores.findAllById(ids).stream()
                .collect(Collectors.toMap(Investigador::getId, Function.identity()));
        var perfiles = new ArrayList<Investigador>(ids.size());
        for (Long id : ids) {
            Investigador perfil = encontrados.get(id);
            if (perfil == null) throw new NoSuchElementException("Investigador no encontrado: " + id);
            perfiles.add(perfil);
        }
        proyecto.setInvestigadores(perfiles);
    }

    // Eliminar
    public void eliminar(Long id) {
        proyectoRepository.deleteById(id);
        auditoriaService.registrar("ELIMINAR", "proyecto", id);
    }
}
