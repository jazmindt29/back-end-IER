packagecom.controller;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link ActividadController}.
 */
@Generated
public class ActividadController__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static ActividadController apply(RegisteredBean registeredBean,
      ActividadController instance) {
    AutowiredFieldValueResolver.forRequiredField("repository").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
