package com.instituto.api.controller;

import com.instituto.api.entity.Contacto;
import com.instituto.api.repository.ContactoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contacto")
public class ContactoController {

    @Autowired
    private ContactoRepository repository;

    @PostMapping
    public Contacto enviarMensaje(@RequestBody Contacto contacto) {
        return repository.save(contacto);
    }
}