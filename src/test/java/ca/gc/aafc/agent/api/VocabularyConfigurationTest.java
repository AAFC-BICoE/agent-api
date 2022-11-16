package ca.gc.aafc.agent.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import ca.gc.aafc.agent.api.vocabulary.AgentVocabularyConfiguration;
import ca.gc.aafc.dina.vocabulary.VocabularyConfiguration;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = AgentModuleApiLauncher.class)
public class VocabularyConfigurationTest extends BaseIntegrationTest {

  @Inject
  private AgentVocabularyConfiguration agentConfiguration;

  @Test
  void identifiers() {
    List<VocabularyConfiguration.VocabularyElement> coordinateSystem = agentConfiguration.getVocabulary()
      .get("identifiers");
    assertEquals(2, coordinateSystem.size());
    coordinateSystem.forEach(assertVocabElement());
  }

  private static Consumer<VocabularyConfiguration.VocabularyElement> assertVocabElement() {
    return vocabularyElement -> {
      assertNotNull(vocabularyElement.getName());
      assertNotNull(vocabularyElement.getTerm());
      assertNotNull(vocabularyElement.getLabels());
    };
  }
}
