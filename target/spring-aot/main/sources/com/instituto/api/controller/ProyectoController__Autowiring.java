packagecom.controller;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link ProyectoController}.
 */
@Generated
public class ProyectoController__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static ProyectoController apply(RegisteredBean registeredBean,
      ProyectoController instance) {
    AutowiredFieldValueResolver.forRequiredField("proyectoService").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
