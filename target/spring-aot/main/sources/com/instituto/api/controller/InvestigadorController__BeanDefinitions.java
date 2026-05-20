package com.instituto.api.controller;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link InvestigadorController}.
 */
@Generated
public class InvestigadorController__BeanDefinitions {
  /**
   * Get the bean definition for 'investigadorController'.
   */
  public static BeanDefinition getInvestigadorControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(InvestigadorController.class);
    InstanceSupplier<InvestigadorController> instanceSupplier = InstanceSupplier.using(InvestigadorController::new);
    instanceSupplier = instanceSupplier.andThen(InvestigadorController__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
