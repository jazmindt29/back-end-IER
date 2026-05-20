packagecom.controller;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link InvestigadorController}.
 */
@Generated
public class InvestigadorController__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static InvestigadorController apply(RegisteredBean registeredBean,
      InvestigadorController instance) {
    AutowiredFieldValueResolver.forRequiredField("service").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
