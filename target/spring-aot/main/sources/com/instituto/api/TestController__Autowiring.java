package com. ;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.AutowiredFieldValueResolver;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Autowiring for {@link TestController}.
 */
@Generated
public class TestController__Autowiring {
  /**
   * Apply the autowiring.
   */
  public static TestController apply(RegisteredBean registeredBean, TestController instance) {
    AutowiredFieldValueResolver.forRequiredField("rolRepo").resolveAndSet(registeredBean, instance);
    return instance;
  }
}
