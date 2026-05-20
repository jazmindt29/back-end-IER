package com.instituto.api.controller;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link LaboratorioController}.
 */
@Generated
public class LaboratorioController__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static LaboratorioController apply(RegisteredBean registeredBean,
      LaboratorioController instance) {
    AutowiredFieldValueResolver.forRequiredField("service").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
