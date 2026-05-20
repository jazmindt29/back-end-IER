packagecom.controller;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link ProyectoController}.
 */
@Generated
public class ProyectoController__BeanDefinitions {
  /**
   * Get the bean definition for 'proyectoController'.
   */
  public static BeanDefinition getProyectoControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ProyectoController.class);
    InstanceSupplier<ProyectoController> instanceSupplier = InstanceSupplier.using(ProyectoController::new);
    instanceSupplier = instanceSupplier.andThen(ProyectoController__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
