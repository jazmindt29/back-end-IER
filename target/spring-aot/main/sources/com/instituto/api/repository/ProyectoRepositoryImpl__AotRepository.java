packagecom.repository;

importcom.entity.Proyecto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.lang.String;
import java.util.List;
import org.springframework.aot.generate.Generated;
import org.springframework.data.jpa.repository.aot.AotRepositoryFragmentSupport;
import org.springframework.data.jpa.repository.query.QueryEnhancerSelector;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;

/**
 * AOT generated JPA repository implementation for {@link ProyectoRepository}.
 */
@Generated
public class ProyectoRepositoryImpl__AotRepository extends AotRepositoryFragmentSupport {
  private final RepositoryFactoryBeanSupport.FragmentCreationContext context;

  private final EntityManager entityManager;

  public ProyectoRepositoryImpl__AotRepository(EntityManager entityManager,
      RepositoryFactoryBeanSupport.FragmentCreationContext context) {
    super(QueryEnhancerSelector.DEFAULT_SELECTOR, context);
    this.entityManager = entityManager;
    this.context = context;
  }

  /**
   * AOT generated implementation of {@link ProyectoRepository#findByEsDestacadoTrue()}.
   */
  public List<Proyecto> findByEsDestacadoTrue() {
    String queryString = "SELECT p FROM Proyecto p WHERE p.esDestacado = TRUE";
    Query query = this.entityManager.createQuery(queryString);

    return (List<Proyecto>) query.getResultList();
  }
}
