package com.instituto.api.service;

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

    @Autowired
    private AuditoriaService auditoriaService;

    public List<Investigador> listarTodos() { return investigadorRepository.findAll(); }
    public Optional<Investigador> buscarPorId(Long id) { return investigadorRepository.findById(id); }
    public List<Investigador> buscarPorArea(String area) { return investigadorRepository.findByArea(area); }

    public Investigador guardar(Investigador investigador) {
        boolean esNuevo = investigador.getId() == null;
        investigador.setUpdatedAt(LocalDateTime.now());
        Investigador guardado = investigadorRepository.save(investigador);
        auditoriaService.registrar(esNuevo ? "CREAR" : "ACTUALIZAR", "investigador", guardado.getId());
        return guardado;
    }

    public void eliminar(Long id) {
        investigadorRepository.deleteById(id);
        auditoriaService.registrar("ELIMINAR", "investigador", id);
    }
}
