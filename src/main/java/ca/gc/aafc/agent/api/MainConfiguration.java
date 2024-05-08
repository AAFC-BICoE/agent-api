package ca.gc.aafc.agent.api;

import ca.gc.aafc.dina.DinaBaseApiAutoConfiguration;
import ca.gc.aafc.dina.messaging.producer.DocumentOperationNotificationMessageProducer;
import ca.gc.aafc.dina.messaging.producer.LogBasedMessageProducer;
import ca.gc.aafc.dina.service.JaversDataService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = DinaBaseApiAutoConfiguration.class)
@ImportAutoConfiguration(DinaBaseApiAutoConfiguration.class)
@MapperScan(basePackageClasses = JaversDataService.class)
public class MainConfiguration {

  /**
   * Provides a fallback MessageProducer when messaging.isProducer is false.
   */
  @Configuration
  public static class FallbackMessageProducer {

    @Bean
    @ConditionalOnProperty(name = "dina.messaging.isProducer", havingValue = "false")
    public DocumentOperationNotificationMessageProducer init() {
      return new LogBasedMessageProducer();
    }
  }
}
