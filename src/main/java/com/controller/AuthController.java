package com.instituto.api.controller;

import com.instituto.api.dto.AuthRequest;
import com.instituto.api.repository.UsuarioRepository;
import com.instituto.api.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest datos) {
        // Buscamos por username porque así se llama tu columna en pgAdmin
        return usuarioRepository.findByUsername(datos.getEmail())
            .map(user -> {
                // Verificamos contraseña tal cual está en tu base de datos
                if (user.getPassword().equals(datos.getPassword())) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("id", user.getId());
                    
                    // Extraemos el nombre del rol desde la relación
                    if (user.getRol() != null) {
                        response.put("role", user.getRol().getNombre());
                    } else {
                        response.put("role", "usuario");
                    }
                    
                    return ResponseEntity.ok(response);
                }
                return ResponseEntity.status(401).body("Contraseña incorrecta");
            })
            .orElse(ResponseEntity.status(404).body("Usuario no encontrado"));
    }
}