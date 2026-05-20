package com.service;

import com.entity.Rol;
import com.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RolService {
    @Autowired
    private RolRepository repo;

    public List<Rol> listar() { return repo.findAll(); }
    
    public Optional<Rol> buscarPorId(Long id) { return repo.findById(id); }

    public Rol guardar(Rol rol) { return repo.save(rol); }

    public void eliminar(Long id) { repo.deleteById(id); }
}