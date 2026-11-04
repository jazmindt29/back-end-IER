package com.instituto.api.security;

import com.instituto.api.repository.UsuarioRepository;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

/** Revoca el acceso al desactivar, borrar o cambiar el rol de una cuenta. */
public class CuentaActivaValidator implements OAuth2TokenValidator<Jwt> {
    private final UsuarioRepository usuarios;

    public CuentaActivaValidator(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        Object id = jwt.getClaim("userId");
        if (id instanceof Number numero && usuarios.findById(numero.longValue())
                .filter(u -> Boolean.TRUE.equals(u.getEnable()))
                .filter(u -> u.getUsername().equals(jwt.getSubject()))
                .filter(u -> u.getRol() != null && u.getRol().getNombre().equals(jwt.getClaimAsString("rol")))
                .isPresent()) {
            return OAuth2TokenValidatorResult.success();
        }
        return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "La sesión ya no está activa", null));
    }
}
