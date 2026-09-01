package com.instituto.api.dto;

public record LoginResponse(
        String token,
        String username,
        String rol,
        Long userId,
        Long investigadorId) {
}
