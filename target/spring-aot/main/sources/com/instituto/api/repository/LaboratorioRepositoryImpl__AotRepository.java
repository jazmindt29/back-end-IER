packagecom.repository;

importcom.entity.Laboratorio;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.lang.String;
import java.util.List;
import org.springframework.aot.generate.Generated;
import org.springframework.data.jpa.repository.aot.AotRepositoryFragmentSupport;
import org.springframework.data.jpa.repository.query.QueryEnhancerSelector;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;

/**
 * AOT generated JPA repository implementation for {@link LaboratorioRepository}.
 */
@Generated
public class LaboratorioRepositoryImpl__AotRepository extends AotRepositoryFragmentSupport {
  private final RepositoryFactoryBeanSupport.FragmentCreationContext context;

  private final EntityManager entityManager;

  public LaboratorioRepositoryImpl__AotRepository(EntityManager entityManager,
      RepositoryFactoryBeanSupport.FragmentCreationContext context) {
    super(QueryEnhancerSelector.DEFAULT_SELECTOR, context);
    this.entityManager = entityManager;
    this.context = context;
  }

  /**
   * AOT generated implementation of {@link LaboratorioRepository#findByNombreContainingIgnoreCase(java.lang.String)}.
   */
  public List<Laboratorio> findByNombreContainingIgnoreCase(String nombre) {
    String queryString = "SELECT l FROM Laboratorio l WHERE UPPER(l.nombre) LIKE UPPER(:nombre) ESCAPE '\\'";
    Query query = this.entityManager.createQuery(queryString);
    query.setParameter("nombre", "%%%s%%".formatted(nombre != null ? nombre.toUpperCase() : nombre));

    return (List<Laboratorio>) query.getResultList();
  }
}
