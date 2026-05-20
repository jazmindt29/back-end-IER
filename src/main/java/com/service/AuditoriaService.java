package com.instituto.api.service;

import com.instituto.api.entity.Auditoria;
import com.instituto.api.repository.AuditoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AuditoriaService {
    @Autowired
    private AuditoriaRepository repo;

    public List<Auditoria> listarTodas() { return repo.findAll(); }

    public Auditoria registrar(Auditoria auditoria) { return repo.save(auditoria); }
}