packagecom.service;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link LaboratorioService}.
 */
@Generated
public class LaboratorioService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static LaboratorioService apply(RegisteredBean registeredBean,
      LaboratorioService instance) {
    AutowiredFieldValueResolver.forRequiredField("repository").resolveAndSet(registeredBean, instance);
    AutowiredFieldValueResolver.forRequiredField("componenteRepository").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
