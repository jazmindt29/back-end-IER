package com.instituto.api.controller;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link ContactoController}.
 */
@Generated
public class ContactoController__BeanDefinitions {
  /**
   * Get the bean definition for 'contactoController'.
   */
  public static BeanDefinition getContactoControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ContactoController.class);
    InstanceSupplier<ContactoController> instanceSupplier = InstanceSupplier.using(ContactoController::new);
    instanceSupplier = instanceSupplier.andThen(ContactoController__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
