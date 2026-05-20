package com.instituto.api.repository;

import com.instituto.api.entity.AreaInvestigacion;
import com.instituto.api.entity.Investigador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InvestigadorRepository extends JpaRepository<Investigador, Long> {
    
    // Spring generará automáticamente la consulta SQL: 
    // SELECT * FROM investigadores WHERE area = ?
    List<Investigador> findByArea(AreaInvestigacion area);
    
    // Por si acaso quieres buscar por especialidad también
    List<Investigador> findByEspecialidad(String especialidad);
}