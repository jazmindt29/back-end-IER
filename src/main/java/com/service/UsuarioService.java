package com.instituto.api.service;

import com.instituto.api.entity.Investigador;
import com.instituto.api.entity.Usuario;
import com.instituto.api.repository.InvestigadorRepository;
import com.instituto.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importante
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository repo; // Usaremos 'repo' en todo el archivo
    
    @Autowired
    private InvestigadorRepository investigadorRepository;

    @Transactional // Esto asegura que se guarden AMBOS o NINGUNO en caso de error
    public Usuario registrarUsuario(Usuario usuario) {
        // 1. Guardamos primero el usuario usando el nombre correcto 'repo'
        Usuario nuevoUsuario = repo.save(usuario);

        // 2. Si el rol es el 4 (INVESTIGADOR), creamos su perfil vacío
        if (nuevoUsuario.getRol() != null && nuevoUsuario.getRol().getId() == 4) {
            Investigador perfil = new Investigador();
            perfil.setUsuario(nuevoUsuario);
            // Esto vincula la cuenta con la tabla de investigadores
            perfil.setNombre(nuevoUsuario.getUsername()); 
            investigadorRepository.save(perfil);
        }

        return nuevoUsuario;
    }

    public List<Usuario> listar() { return repo.findAll(); }

    public Optional<Usuario> buscarPorUsername(String username) {
        return repo.findByUsername(username);
    }

    public Usuario guardar(Usuario usuario) {
        return repo.save(usuario);
    }

    public void eliminar(Long id) { repo.deleteById(id); }
}