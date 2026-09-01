package com.instituto.api.controller;

import com.instituto.api.entity.Contacto;
import com.instituto.api.repository.ContactoRepository;
import com.instituto.api.service.MailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contacto")
public class ContactoController {

    @Autowired
    private ContactoRepository repository;

    @Autowired
    private MailService mailService;

    // Público: guarda el mensaje y notifica por correo al departamento
    @PostMapping
    public Contacto enviarMensaje(@Valid @RequestBody Contacto contacto) {
        Contacto guardado = repository.save(contacto);
        mailService.notificarContacto(guardado);
        return guardado;
    }
}
