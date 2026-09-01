package com.instituto.api.service;

import com.instituto.api.entity.Investigador;
import com.instituto.api.entity.Usuario;
import com.instituto.api.repository.InvestigadorRepository;
import com.instituto.api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InvestigadorService {

    private final InvestigadorRepository investigadorRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;

    public InvestigadorService(
            InvestigadorRepository investigadorRepository,
            UsuarioRepository usuarioRepository,
            AuditoriaService auditoriaService) {

        this.investigadorRepository = investigadorRepository;
        this.usuarioRepository = usuarioRepository;
        this.auditoriaService = auditoriaService;
    }

    // ============================================================
    // LISTAR TODOS
    // ============================================================
    public List<Investigador> listarTodos() {
        return investigadorRepository.findAll();
    }

    // ============================================================
    // BUSCAR POR ID
    // ============================================================
    public Optional<Investigador> buscarPorId(Long id) {
        return investigadorRepository.findById(id);
    }

    // ============================================================
    // BUSCAR POR ÁREA
    // ============================================================
    public List<Investigador> buscarPorArea(String area) {
        return investigadorRepository.findByArea(area);
    }

    // ============================================================
    // GUARDAR / ACTUALIZAR (ADMIN / GENERAL)
    // ============================================================
    public Investigador guardar(Investigador investigador) {
        boolean esNuevo = investigador.getId() == null;
        investigador.setUpdatedAt(LocalDateTime.now());

        Investigador guardado = investigadorRepository.save(investigador);

        auditoriaService.registrar(
                esNuevo ? "CREAR" : "ACTUALIZAR",
                "investigador",
                guardado.getId()
        );

        return guardado;
    }

    // ============================================================
    // ELIMINAR
    // ============================================================
    public void eliminar(Long id) {
        investigadorRepository.deleteById(id);

        auditoriaService.registrar(
                "ELIMINAR",
                "investigador",
                id
        );
    }

    // ============================================================
    // OBTENER MI PERFIL (SESIÓN ACTUAL)
    // ============================================================
    @Transactional(readOnly = true)
public Investigador obtenerMiPerfil(String username) {
    Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

    return investigadorRepository.findByUsuarioId(usuario.getId())
            .orElseGet(() -> {
                Investigador temp = new Investigador();
                temp.setNombre(usuario.getUsername()); // Usamos username directamente
                temp.setCorreo(usuario.getUsername());
                temp.setUsuario(usuario);
                return temp;
            });

    }

    // ============================================================
    // GUARDAR O ACTUALIZAR MI PERFIL (SESIÓN ACTUAL)
    // ============================================================
    @Transactional
    public Investigador guardarOActualizarMiPerfil(String username, Investigador datos) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        Investigador investigador = investigadorRepository.findByUsuarioId(usuario.getId())
                .orElseGet(() -> {
                    Investigador nuevo = new Investigador();
                    nuevo.setUsuario(usuario);
                    return nuevo;
                });

        boolean esNuevo = investigador.getId() == null;

        // Asignación de datos
        investigador.setNombre(datos.getNombre());
        investigador.setGradoAcademico(datos.getGradoAcademico());
        investigador.setArea(datos.getArea());
        investigador.setEspecialidad(datos.getEspecialidad());
        investigador.setCorreo(datos.getCorreo());
        investigador.setFotoUrl(datos.getFotoUrl());
        investigador.setBioCorta(datos.getBioCorta());
        investigador.setBiografia(datos.getBiografia());
        investigador.setUpdatedAt(LocalDateTime.now());

        Investigador guardado = investigadorRepository.save(investigador);

        auditoriaService.registrar(
                esNuevo ? "CREAR" : "ACTUALIZAR",
                "investigador_mi_perfil",
                guardado.getId()
        );

        return guardado;
    }
}