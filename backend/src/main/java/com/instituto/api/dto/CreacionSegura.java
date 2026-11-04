package com.instituto.api.dto;

public final class CreacionSegura {
    private CreacionSegura() { }

    public static void sinId(Long id) {
        if (id != null) {
            throw new IllegalArgumentException("No se permite indicar un ID al crear");
        }
    }
}
