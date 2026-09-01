package com.instituto.api.service;

import com.instituto.api.entity.Auditoria;
import com.instituto.api.repository.AuditoriaRepository;
import com.instituto.api.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditoriaService {
    @Autowired
    private AuditoriaRepository repo;

    public List<Auditoria> listarTodas() { return repo.findAll(); }

    /**
     * Registra automáticamente quién (usuario autenticado del SecurityContext)
     * hizo qué (CREAR/ACTUALIZAR/ELIMINAR) sobre qué registro.
     */
    public void registrar(String accion, String entidad, Long entidadId) {
        Auditoria a = new Auditoria();
        a.setCreatedAt(LocalDateTime.now());
        a.setUpdatedBy(JwtService.usuarioActualId());
        a.setAccion(accion);
        a.setEntidad(entidad);
        a.setEntidadId(entidadId);
        repo.save(a);
    }
}
