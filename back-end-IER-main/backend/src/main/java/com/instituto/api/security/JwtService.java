package com.instituto.api.security;

import com.instituto.api.entity.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private final JwtEncoder encoder;

    @Value("${app.jwt.expiracion-horas}")
    private long expiracionHoras;

    public JwtService(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    public String generarToken(Usuario usuario, Long investigadorId) {
        Instant ahora = Instant.now();
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", usuario.getId());
        if (usuario.getRol() != null) {
            claims.put("rol", usuario.getRol().getNombre());
        }
        if (investigadorId != null) {
            claims.put("investigadorId", investigadorId);
        }
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("ier-api")
                .subject(usuario.getUsername())
                .issuedAt(ahora)
                .expiresAt(ahora.plus(expiracionHoras, ChronoUnit.HOURS))
                .claims(c -> c.putAll(claims))
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claimsSet)).getTokenValue();
    }

    // --- Helpers estáticos para leer el usuario autenticado desde cualquier servicio ---

    private static Jwt jwtActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            return jwt;
        }
        return null;
    }

    private static Long claimComoLong(String nombre) {
        Jwt jwt = jwtActual();
        if (jwt == null) return null;
        Object valor = jwt.getClaim(nombre);
        return valor instanceof Number n ? n.longValue() : null;
    }

    /** ID del usuario autenticado, o null si no hay sesión. */
    public static Long usuarioActualId() {
        return claimComoLong("userId");
    }

    /** ID del perfil de investigador del usuario autenticado, o null. */
    public static Long investigadorActualId() {
        return claimComoLong("investigadorId");
    }

    /** true si el usuario autenticado tiene rol de administrador. */
    public static boolean esAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().startsWith("ROLE_ADMIN"));
    }
}
