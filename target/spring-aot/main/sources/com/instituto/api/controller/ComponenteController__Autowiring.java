packagecom.controller;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link ComponenteController}.
 */
@Generated
public class ComponenteController__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static ComponenteController apply(RegisteredBean registeredBean,
      ComponenteController instance) {
    AutowiredFieldValueResolver.forRequiredField("repository").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
