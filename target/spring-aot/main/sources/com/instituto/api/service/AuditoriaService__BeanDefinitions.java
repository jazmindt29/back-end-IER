package com.instituto.api.service;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link AuditoriaService}.
 */
@Generated
public class AuditoriaService__BeanDefinitions {
  /**
   * Get the bean definition for 'auditoriaService'.
   */
  public static BeanDefinition getAuditoriaServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(AuditoriaService.class);
    InstanceSupplier<AuditoriaService> instanceSupplier = InstanceSupplier.using(AuditoriaService::new);
    instanceSupplier = instanceSupplier.andThen(AuditoriaService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
