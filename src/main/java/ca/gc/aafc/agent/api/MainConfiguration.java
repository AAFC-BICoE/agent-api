package ca.gc.aafc.agent.api;

import ca.gc.aafc.dina.DinaBaseApiAutoConfiguration;
import ca.gc.aafc.dina.search.messaging.producer.LogBasedMessageProducer;
import ca.gc.aafc.dina.search.messaging.producer.MessageProducer;
import ca.gc.aafc.dina.service.JaversDataService;

import java.util.Arrays;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@ComponentScan(basePackageClasses = DinaBaseApiAutoConfiguration.class)
@ImportAutoConfiguration(DinaBaseApiAutoConfiguration.class)
@MapperScan(basePackageClasses = JaversDataService.class)
public class MainConfiguration {

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
      CorsConfiguration configuration = new CorsConfiguration();
      configuration.setAllowedOrigins(Arrays.asList("*"));
      configuration.setAllowedMethods(Arrays.asList("GET","POST"));
      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", configuration);
      return source;
  }

  /**
   * Provides a fallback MessageProducer when messaging.isProducer is false.
   */
  @Configuration
  public static class FallbackMessageProducer {

    @Bean
    @ConditionalOnProperty(name = "messaging.isProducer", havingValue = "false")
    public MessageProducer init() {
      return new LogBasedMessageProducer();
    }
  }
}
