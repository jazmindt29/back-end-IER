packagecom.service;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link InvestigadorService}.
 */
@Generated
public class InvestigadorService__BeanDefinitions {
  /**
   * Get the bean definition for 'investigadorService'.
   */
  public static BeanDefinition getInvestigadorServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(InvestigadorService.class);
    InstanceSupplier<InvestigadorService> instanceSupplier = InstanceSupplier.using(InvestigadorService::new);
    instanceSupplier = instanceSupplier.andThen(InvestigadorService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
