package ca.gc.aafc.agent.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import ca.gc.aafc.agent.api.dto.AgentDto;
import ca.gc.aafc.dina.DinaBaseApiAutoConfiguration;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.mapper.JpaDtoMapper;
import ca.gc.aafc.dina.repository.SelectionHandler;
import ca.gc.aafc.dina.util.ClassAnnotationHelper;

@Configuration
@ComponentScan(basePackageClasses = DinaBaseApiAutoConfiguration.class)
@ImportAutoConfiguration(DinaBaseApiAutoConfiguration.class)
public class MainConfiguration {

  /**
   * Configures DTO-to-Entity mappings.
   * 
   * @return the DtoJpaMapper
   */
  @Bean
  public JpaDtoMapper dtoJpaMapper(BaseDAO baseDAO) {
    Map<Class<?>, List<JpaDtoMapper.CustomFieldResolverSpec<?>>> customFieldResolvers = new HashMap<>();

    // Map all DTOs to their related Entities.
    Map<Class<?>, Class<?>> entitiesMap = ClassAnnotationHelper
      .findAnnotatedClasses(AgentDto.class, RelatedEntity.class)
      .stream()
      .collect(
        Collectors.toMap(
          Function.identity(),
          clazz -> clazz.getAnnotation(RelatedEntity.class).value()));

    return new JpaDtoMapper(entitiesMap, customFieldResolvers);
  }

}
