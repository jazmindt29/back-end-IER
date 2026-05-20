packagecom.service;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link ProyectoService}.
 */
@Generated
public class ProyectoService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static ProyectoService apply(RegisteredBean registeredBean, ProyectoService instance) {
    AutowiredFieldValueResolver.forRequiredField("proyectoRepository").resolveAndSet(registeredBean, instance);
    AutowiredFieldValueResolver.forRequiredField("investigadorRepository").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
