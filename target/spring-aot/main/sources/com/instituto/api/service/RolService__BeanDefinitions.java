packagecom.service;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link RolService}.
 */
@Generated
public class RolService__BeanDefinitions {
  /**
   * Get the bean definition for 'rolService'.
   */
  public static BeanDefinition getRolServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(RolService.class);
    InstanceSupplier<RolService> instanceSupplier = InstanceSupplier.using(RolService::new);
    instanceSupplier = instanceSupplier.andThen(RolService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
