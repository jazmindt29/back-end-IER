packagecom.controller;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.InstanceSupplier;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link UsuarioController}.
 */
@Generated
public class UsuarioController__BeanDefinitions {
  /**
   * Get the bean definition for 'usuarioController'.
   */
  public static BeanDefinition getUsuarioControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(UsuarioController.class);
    InstanceSupplier<UsuarioController> instanceSupplier = InstanceSupplier.using(UsuarioController::new);
    instanceSupplier = instanceSupplier.andThen(UsuarioController__Autowiring::apply);
    beanDefinition.setInstanceSupplier(instanceSupplier);
    return beanDefinition;
  }
}
