packagecom.controller;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link ActividadController}.
 */
@Generated
public class ActividadController__BeanDefinitions {
  /**
   * Get the bean definition for 'actividadController'.
   */
  public static BeanDefinition getActividadControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ActividadController.class);
    InstanceSupplier<ActividadController> instanceSupplier = InstanceSupplier.using(ActividadController::new);
    instanceSupplier = instanceSupplier.andThen(ActividadController__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
