package ca.gc.aafc.agent.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import ca.gc.aafc.agent.api.dto.AgentDto;
import ca.gc.aafc.agent.api.entities.Agent;
import ca.gc.aafc.dina.DinaBaseApiAutoConfiguration;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.mapper.JpaDtoMapper;
import ca.gc.aafc.dina.repository.SelectionHandler;

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
  public JpaDtoMapper dtoJpaMapper(SelectionHandler selectionHandler, BaseDAO baseDAO) {
    Map<Class<?>, List<JpaDtoMapper.CustomFieldResolverSpec<?>>> customFieldResolvers = new HashMap<>();

    Map<Class<?>, Class<?>> entitiesMap = new HashMap<>();
    entitiesMap.put(AgentDto.class, Agent.class);

    return new JpaDtoMapper(entitiesMap, customFieldResolvers, selectionHandler, baseDAO);
  }

}
