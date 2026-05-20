package com.instituto.api.repository;

import com.instituto.api.entity.AreaInvestigacion;
import com.instituto.api.entity.Investigador;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.lang.String;
import java.util.List;
import org.springframework.aot.generate.Generated;
import org.springframework.data.jpa.repository.aot.AotRepositoryFragmentSupport;
import org.springframework.data.jpa.repository.query.QueryEnhancerSelector;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;

/**
 * AOT generated JPA repository implementation for {@link InvestigadorRepository}.
 */
@Generated
public class InvestigadorRepositoryImpl__AotRepository extends AotRepositoryFragmentSupport {
  private final RepositoryFactoryBeanSupport.FragmentCreationContext context;

  private final EntityManager entityManager;

  public InvestigadorRepositoryImpl__AotRepository(EntityManager entityManager,
      RepositoryFactoryBeanSupport.FragmentCreationContext context) {
    super(QueryEnhancerSelector.DEFAULT_SELECTOR, context);
    this.entityManager = entityManager;
    this.context = context;
  }

  /**
   * AOT generated implementation of {@link InvestigadorRepository#findByArea(com.instituto.api.entity.AreaInvestigacion)}.
   */
  public List<Investigador> findByArea(AreaInvestigacion area) {
    String queryString = "SELECT i FROM Investigador i WHERE i.area = :area";
    Query query = this.entityManager.createQuery(queryString);
    query.setParameter("area", area);

    return (List<Investigador>) query.getResultList();
  }

  /**
   * AOT generated implementation of {@link InvestigadorRepository#findByEspecialidad(java.lang.String)}.
   */
  public List<Investigador> findByEspecialidad(String especialidad) {
    String queryString = "SELECT i FROM Investigador i WHERE i.especialidad = :especialidad";
    Query query = this.entityManager.createQuery(queryString);
    query.setParameter("especialidad", especialidad);

    return (List<Investigador>) query.getResultList();
  }
}
