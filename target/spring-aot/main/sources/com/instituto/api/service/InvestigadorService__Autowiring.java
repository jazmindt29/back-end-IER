packagecom.service;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link InvestigadorService}.
 */
@Generated
public class InvestigadorService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static InvestigadorService apply(RegisteredBean registeredBean,
      InvestigadorService instance) {
    AutowiredFieldValueResolver.forRequiredField("investigadorRepository").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
