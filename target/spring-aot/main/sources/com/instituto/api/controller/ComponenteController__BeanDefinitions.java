package com.instituto.api.controller;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link ComponenteController}.
 */
@Generated
public class ComponenteController__BeanDefinitions {
  /**
   * Get the bean definition for 'componenteController'.
   */
  public static BeanDefinition getComponenteControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ComponenteController.class);
    InstanceSupplier<ComponenteController> instanceSupplier = InstanceSupplier.using(ComponenteController::new);
    instanceSupplier = instanceSupplier.andThen(ComponenteController__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
