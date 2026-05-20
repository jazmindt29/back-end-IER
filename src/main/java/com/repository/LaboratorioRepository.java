package com.repository;

import com.entity.Laboratorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List; // ESTA ES LA LÍNEA QUE FALTA

@Repository
public interface LaboratorioRepository extends JpaRepository<Laboratorio, Long> {
    // Busca por el nombre que definimos (el que coincide con 'text' en pgAdmin)
    List<Laboratorio> findByNombreContainingIgnoreCase(String nombre);
}