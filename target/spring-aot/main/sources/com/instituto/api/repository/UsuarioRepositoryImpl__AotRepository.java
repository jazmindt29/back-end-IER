package com.instituto.api.repository;

import com.instituto.api.entity.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.lang.String;
import java.util.Optional;
import org.springframework.aot.generate.Generated;
import org.springframework.data.jpa.repository.aot.AotRepositoryFragmentSupport;
import org.springframework.data.jpa.repository.query.QueryEnhancerSelector;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;

/**
 * AOT generated JPA repository implementation for {@link UsuarioRepository}.
 */
@Generated
public class UsuarioRepositoryImpl__AotRepository extends AotRepositoryFragmentSupport {
  private final RepositoryFactoryBeanSupport.FragmentCreationContext context;

  private final EntityManager entityManager;

  public UsuarioRepositoryImpl__AotRepository(EntityManager entityManager,
      RepositoryFactoryBeanSupport.FragmentCreationContext context) {
    super(QueryEnhancerSelector.DEFAULT_SELECTOR, context);
    this.entityManager = entityManager;
    this.context = context;
  }

  /**
   * AOT generated implementation of {@link UsuarioRepository#findByUsername(java.lang.String)}.
   */
  public Optional<Usuario> findByUsername(String username) {
    String queryString = "SELECT u FROM Usuario u WHERE u.username = :username";
    Query query = this.entityManager.createQuery(queryString);
    query.setParameter("username", username);

    return Optional.ofNullable((Usuario) convertOne(query.getSingleResultOrNull(), false, Usuario.class));
  }
}
