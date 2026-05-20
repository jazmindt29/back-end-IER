package com.instituto.api.service;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link AuditoriaService}.
 */
@Generated
public class AuditoriaService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static AuditoriaService apply(RegisteredBean registeredBean, AuditoriaService instance) {
    AutowiredFieldValueResolver.forRequiredField("repo").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
