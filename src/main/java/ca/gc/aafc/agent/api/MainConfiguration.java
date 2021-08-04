package ca.gc.aafc.agent.api;

import ca.gc.aafc.dina.DinaBaseApiAutoConfiguration;
import ca.gc.aafc.dina.search.common.config.YAMLConfigProperties;
import ca.gc.aafc.dina.search.messaging.producer.MessageProducer;
import ca.gc.aafc.dina.search.messaging.types.DocumentOperationNotification;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = DinaBaseApiAutoConfiguration.class)
@ImportAutoConfiguration(DinaBaseApiAutoConfiguration.class)
public class MainConfiguration {

  /**
   * Provides a fallback MessageProducer when messaging.isProducer is not true
   * To be replaced by LogBasedMessageProducer in search-messaging 0.3
   */
  @Configuration
  public static class FallbackMessageProducer {

    @Bean
    @ConditionalOnMissingBean
    public MessageProducer init(YAMLConfigProperties yamlConfigProps) {
      return new MessageProducer(null, yamlConfigProps) {
        @Override
        public void send(DocumentOperationNotification documentOperationNotification) {
         // ignore
        }
      };
    }
  }
}
