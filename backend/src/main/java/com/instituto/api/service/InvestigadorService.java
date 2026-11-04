package com.instituto.api.service;

import com.instituto.api.entity.Investigador;
import com.instituto.api.entity.Usuario;
import com.instituto.api.repository.InvestigadorRepository;
import com.instituto.api.repository.UsuarioRepository;
import com.instituto.api.repository.LaboratorioRepository;
import com.instituto.api.repository.ProyectoRepository;
import com.instituto.api.repository.ReservaLaboratorioRepository;
import com.instituto.api.repository.AvisoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InvestigadorService {
    private final LaboratorioRepository laboratorios;
    private final ProyectoRepository proyectos;
    private final ReservaLaboratorioRepository reservas;
    private final AvisoRepository avisos;
    private final InvestigadorRepository investigadorRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;

    public InvestigadorService(LaboratorioRepository laboratorios, ProyectoRepository proyectos,
                              ReservaLaboratorioRepository reservas, AvisoRepository avisos,
                              InvestigadorRepository investigadorRepository,
                              UsuarioRepository usuarioRepository, AuditoriaService auditoriaService) {
        this.laboratorios = laboratorios;
        this.proyectos = proyectos;
        this.reservas = reservas;
        this.avisos = avisos;
        this.investigadorRepository = investigadorRepository;
        this.usuarioRepository = usuarioRepository;
        this.auditoriaService = auditoriaService;
    }

    public List<Investigador> listarTodos() { 
        return investigadorRepository.findAll(); 
    }

    public Optional<Investigador> buscarPorId(Long id) { 
        return investigadorRepository.findById(id); 
    }

    public List<Investigador> buscarPorArea(String area) { 
        return investigadorRepository.findByArea(area); 
    }

    public Investigador guardar(Investigador investigador) {
        boolean esNuevo = investigador.getId() == null;
        investigador.setUpdatedAt(LocalDateTime.now());
        Investigador guardado = investigadorRepository.save(investigador);
        auditoriaService.registrar(esNuevo ? "CREAR" : "ACTUALIZAR", "investigador", guardado.getId());
        return guardado;
    }

    @Transactional
    public void eliminar(Long id) {
        Investigador investigador = investigadorRepository.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("Investigador no encontrado"));

        if (laboratorios.existsByEncargadoId(id) || proyectos.existsByInvestigadoresId(id)
                || reservas.existsByInvestigadorId(id)) {
            throw new IllegalStateException("El investigador tiene laboratorios, proyectos o reservas vinculados");
        }

        Usuario usuarioAsociado = investigador.getUsuario();
        if (usuarioAsociado != null && avisos.existsByAutorId(usuarioAsociado.getId())) {
            throw new IllegalStateException("La cuenta tiene publicaciones vinculadas");
        }

        // 1. Elimina el perfil público del investigador
        investigadorRepository.delete(investigador);

        // 2. Elimina la cuenta en la tabla 'users' para liberar el correo
        if (usuarioAsociado != null) {
            usuarioRepository.delete(usuarioAsociado);
        }

        auditoriaService.registrar("ELIMINAR", "investigador", id);
    }
}
