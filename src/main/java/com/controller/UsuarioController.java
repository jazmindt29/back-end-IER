package com.controller;

import com.entity.Usuario;
import com.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    // Obtener todos los usuarios (incluye sus roles)
    @GetMapping
    public List<Usuario> listar() {
        return service.listar();
    }

    // Buscar un usuario por su Username
    @GetMapping("/{username}")
    public ResponseEntity<Usuario> buscarPorUsername(@PathVariable String username) {
        return service.buscarPorUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Registrar un nuevo usuario (Admin o Investigador)
    // Registrar un nuevo usuario (Admin o Investigador)
    @PostMapping
    public Usuario registrar(@RequestBody Usuario usuario) {
        // CAMBIO AQUÍ: Usar registrarUsuario para que ejecute la lógica del perfil
        return service.registrarUsuario(usuario); 
    }

    // Eliminar un usuario por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}