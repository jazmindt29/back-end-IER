package com.instituto.api.dto;

import com.instituto.api.entity.AreaInvestigacion;
import lombok.Data;

@Data
public class InvestigadorDTO {
    private String nombre;
    private AreaInvestigacion area;
    private String especialidad;
    private String grado;
    private String bio;
    private String biografia;
    private String fotoUrl;
    private String correoInstitucional;
}
