package com.instituto.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    // WRITE_ONLY: se acepta en el JSON de entrada pero nunca se serializa hacia el cliente
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;

    // Cambiado a 'enable' para coincidir exactamente con pgAdmin
    private Boolean enable;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Flujo de invitación: token único con expiración (48h) para que el investigador
    // establezca su propia contraseña. Nunca se expone en las respuestas JSON.
    @JsonIgnore
    @Column(name = "token_invitacion")
    private String tokenInvitacion;

    @JsonIgnore
    @Column(name = "fecha_expiracion_token")
    private LocalDateTime fechaExpiracionToken;

    // Relación con el Rol (usando la columna que conecta con la tabla roles)
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Rol rol;
}
