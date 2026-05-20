package com.instituto.api.service;

import com.instituto.api.entity.Laboratorio;
import com.instituto.api.entity.Componente;
import com.instituto.api.repository.LaboratorioRepository;
import com.instituto.api.repository.ComponenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class LaboratorioService {

    @Autowired
    private LaboratorioRepository repository;

    @Autowired
    private ComponenteRepository componenteRepository; // Inyectamos esto para buscar componentes

    public List<Laboratorio> listar() { 
        return repository.findAll(); 
    }

    public Optional<Laboratorio> buscarPorId(Long id) { 
        return repository.findById(id); 
    }

    public Laboratorio guardar(Laboratorio lab) { 
        return repository.save(lab); 
    }

    public void eliminar(Long id) { 
        repository.deleteById(id); 
    }

    // NUEVO MÉTODO: Para asignar un componente existente a un laboratorio
    public Laboratorio asignarComponente(Long labId, Long componenteId) {
        Laboratorio lab = repository.findById(labId)
                .orElseThrow(() -> new RuntimeException("Laboratorio no encontrado"));
        Componente comp = componenteRepository.findById(componenteId)
                .orElseThrow(() -> new RuntimeException("Componente no encontrado"));
        
        lab.getComponentes().add(comp);
        return repository.save(lab);
    }
}