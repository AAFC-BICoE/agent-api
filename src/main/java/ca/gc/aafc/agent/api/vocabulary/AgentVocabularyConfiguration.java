package ca.gc.aafc.agent.api.vocabulary;

import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import ca.gc.aafc.dina.property.YamlPropertyLoaderFactory;
import ca.gc.aafc.dina.vocabulary.VocabularyConfiguration;

@Configuration
@PropertySource(value = "classpath:vocabulary/identifiers.yml", factory = YamlPropertyLoaderFactory.class)
@ConfigurationProperties
public class AgentVocabularyConfiguration extends VocabularyConfiguration<VocabularyConfiguration.VocabularyElement> {

    public AgentVocabularyConfiguration(Map<String, List<VocabularyElement>> vocabulary) {
        super(vocabulary);
    }
    
}
