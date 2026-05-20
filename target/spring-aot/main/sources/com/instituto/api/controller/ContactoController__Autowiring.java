packagecom.controller;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link ContactoController}.
 */
@Generated
public class ContactoController__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static ContactoController apply(RegisteredBean registeredBean,
      ContactoController instance) {
    AutowiredFieldValueResolver.forRequiredField("repository").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
