package com.instituto.api.service;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link ProyectoService}.
 */
@Generated
public class ProyectoService__BeanDefinitions {
  /**
   * Get the bean definition for 'proyectoService'.
   */
  public static BeanDefinition getProyectoServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ProyectoService.class);
    InstanceSupplier<ProyectoService> instanceSupplier = InstanceSupplier.using(ProyectoService::new);
    instanceSupplier = instanceSupplier.andThen(ProyectoService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
