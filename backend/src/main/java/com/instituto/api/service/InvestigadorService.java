package com.instituto.api.service;

import com.instituto.api.entity.Investigador;
import com.instituto.api.entity.Usuario;
import com.instituto.api.repository.InvestigadorRepository;
import com.instituto.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InvestigadorService {

    @Autowired
    private InvestigadorRepository investigadorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AuditoriaService auditoriaService;

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
                .orElseThrow(() -> new RuntimeException("Investigador no encontrado con ID: " + id));

        Usuario usuarioAsociado = investigador.getUsuario();

        // 1. Elimina el perfil público del investigador
        investigadorRepository.delete(investigador);

        // 2. Elimina la cuenta en la tabla 'users' para liberar el correo
        if (usuarioAsociado != null) {
            usuarioRepository.delete(usuarioAsociado);
        }

        auditoriaService.registrar("ELIMINAR", "investigador", id);
    }
}