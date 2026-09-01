package com.instituto.api.service;

import com.instituto.api.entity.Investigador;
import com.instituto.api.entity.Usuario;
import com.instituto.api.repository.InvestigadorRepository;
import com.instituto.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importante
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repo;

    @Autowired
    private InvestigadorRepository investigadorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Transaccional: se guardan usuario y perfil, o ninguno
    @Transactional
    public Usuario registrarUsuario(Usuario usuario) {
        // Nunca se persiste una contraseña en texto plano
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        if (usuario.getCreatedAt() == null) {
            usuario.setCreatedAt(LocalDateTime.now());
        }
        Usuario nuevoUsuario = repo.save(usuario);

        // Si el rol es el 4 (INVESTIGADOR), se crea su perfil vinculado
        if (nuevoUsuario.getRol() != null && nuevoUsuario.getRol().getId() == 4) {
            Investigador perfil = new Investigador();
            perfil.setUsuario(nuevoUsuario);
            perfil.setNombre(nuevoUsuario.getUsername());
            investigadorRepository.save(perfil);
        }

        return nuevoUsuario;
    }

    public List<Usuario> listar() { return repo.findAll(); }

    public Optional<Usuario> buscarPorUsername(String username) {
        return repo.findByUsername(username);
    }

    public void eliminar(Long id) { repo.deleteById(id); }
}
