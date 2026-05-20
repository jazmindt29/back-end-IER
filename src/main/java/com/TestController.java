package com;

import com.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired; // El import que faltaba
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private RolRepository rolRepo; 

    @GetMapping("/check-roles")
    public String check() {
        try {
            long count = rolRepo.count();
            return "¡Conexión exitosa! El backend leyó " + count + " rol(es) de la base de datos.";
        } catch (Exception e) {
            return "Error de comunicación con la BD: " + e.getMessage();
        }
    }
}