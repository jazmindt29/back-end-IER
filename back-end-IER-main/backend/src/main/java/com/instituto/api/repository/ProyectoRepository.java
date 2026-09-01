package com.instituto.api.repository;

import com.instituto.api.entity.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {
    // Método personalizado: Spring entiende "FindBy" + "EsDestacado" + "True"
    List<Proyecto> findByEsDestacadoTrue();
}