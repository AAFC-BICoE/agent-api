package ca.gc.aafc.agent.api.config;

import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

import ca.gc.aafc.dina.property.YamlPropertyLoaderFactory;
import ca.gc.aafc.dina.vocabulary.VocabularyConfiguration;
import ca.gc.aafc.dina.vocabulary.VocabularyElementConfiguration;

@Configuration
@PropertySource(value = "classpath:vocabulary/identifiers.yml", factory = YamlPropertyLoaderFactory.class)
@ConfigurationProperties
@Validated
public class AgentVocabularyConfiguration extends VocabularyConfiguration<VocabularyElementConfiguration> {

  public static final String IDENTIFIERS = "identifiers";

  public AgentVocabularyConfiguration(Map<String, List<VocabularyElementConfiguration>> vocabulary) {
    super(vocabulary);
  }

}
