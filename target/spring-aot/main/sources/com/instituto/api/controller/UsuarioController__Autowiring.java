packagecom.controller;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link UsuarioController}.
 */
@Generated
public class UsuarioController__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static UsuarioController apply(RegisteredBean registeredBean, UsuarioController instance) {
    AutowiredFieldValueResolver.forRequiredField("service").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
