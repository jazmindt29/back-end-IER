package com.instituto.api.controller;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link LaboratorioController}.
 */
@Generated
public class LaboratorioController__BeanDefinitions {
  /**
   * Get the bean definition for 'laboratorioController'.
   */
  public static BeanDefinition getLaboratorioControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(LaboratorioController.class);
    InstanceSupplier<LaboratorioController> instanceSupplier = InstanceSupplier.using(LaboratorioController::new);
    instanceSupplier = instanceSupplier.andThen(LaboratorioController__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
