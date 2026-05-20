packagecom.service;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link LaboratorioService}.
 */
@Generated
public class LaboratorioService__BeanDefinitions {
  /**
   * Get the bean definition for 'laboratorioService'.
   */
  public static BeanDefinition getLaboratorioServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(LaboratorioService.class);
    InstanceSupplier<LaboratorioService> instanceSupplier = InstanceSupplier.using(LaboratorioService::new);
    instanceSupplier = instanceSupplier.andThen(LaboratorioService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
