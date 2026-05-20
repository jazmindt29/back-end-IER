package com. ;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link ApiApplication}.
 */
@Generated
public class ApiApplication__BeanDefinitions {
  /**
   * Get the bean definition for 'apiApplication'.
   */
  public static BeanDefinition getApiApplicationBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ApiApplication.class);
    beanDefinition.setInstanceSupplier(ApiApplication::new);
    return beanDefinition;
  }
}
