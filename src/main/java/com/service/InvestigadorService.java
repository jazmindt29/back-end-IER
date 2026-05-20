package com.instituto.api.service;

import com.instituto.api.dto.InvestigadorDTO;
import com.instituto.api.entity.AreaInvestigacion;
import com.instituto.api.entity.Investigador;
import com.instituto.api.repository.InvestigadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InvestigadorService {
    @Autowired
    private InvestigadorRepository investigadorRepository;

    public List<Investigador> listarTodos() { return investigadorRepository.findAll(); }
    public Optional<Investigador> buscarPorId(Long id) { return investigadorRepository.findById(id); }
    public Investigador obtenerPorId(Long id) { 
        return investigadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Investigador no encontrado"));
    }
    public List<Investigador> buscarPorArea(AreaInvestigacion area) {return investigadorRepository.findByArea(area);}
    public Investigador guardar(Investigador investigador) { return investigadorRepository.save(investigador); }
    public Investigador crearDesdeDTO(InvestigadorDTO dto) {
        Investigador investigador = new Investigador();
        investigador.setNombre(dto.getNombre());
        investigador.setArea(dto.getArea());
        investigador.setEspecialidad(dto.getEspecialidad());
        investigador.setGrado(dto.getGrado());
        investigador.setBio(dto.getBio());
        investigador.setBiografia(dto.getBiografia());
        investigador.setFotoUrl(dto.getFotoUrl());
        investigador.setCorreoInstitucional(dto.getCorreoInstitucional());
        investigador.setUpdatedAt(LocalDateTime.now());
        return investigadorRepository.save(investigador);
    }
    public Investigador actualizar(Long id, InvestigadorDTO dto) {
        Investigador investigador = obtenerPorId(id);
        investigador.setNombre(dto.getNombre());
        investigador.setArea(dto.getArea());
        investigador.setEspecialidad(dto.getEspecialidad());
        investigador.setGrado(dto.getGrado());
        investigador.setBio(dto.getBio());
        investigador.setBiografia(dto.getBiografia());
        investigador.setFotoUrl(dto.getFotoUrl());
        investigador.setCorreoInstitucional(dto.getCorreoInstitucional());
        investigador.setUpdatedAt(LocalDateTime.now());
        return investigadorRepository.save(investigador);
    }
    public void eliminar(Long id) { investigadorRepository.deleteById(id); }
}