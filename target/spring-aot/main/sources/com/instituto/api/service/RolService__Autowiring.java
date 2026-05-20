package com.instituto.api.service;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link RolService}.
 */
@Generated
public class RolService__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static RolService apply(RegisteredBean registeredBean, RolService instance) {
    AutowiredFieldValueResolver.forRequiredField("repo").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
