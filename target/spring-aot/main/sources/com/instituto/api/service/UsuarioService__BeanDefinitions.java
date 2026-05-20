packagecom.service;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link UsuarioService}.
 */
@Generated
public class UsuarioService__BeanDefinitions {
  /**
   * Get the bean definition for 'usuarioService'.
   */
  public static BeanDefinition getUsuarioServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(UsuarioService.class);
    InstanceSupplier<UsuarioService> instanceSupplier = InstanceSupplier.using(UsuarioService::new);
    instanceSupplier = instanceSupplier.andThen(UsuarioService__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
