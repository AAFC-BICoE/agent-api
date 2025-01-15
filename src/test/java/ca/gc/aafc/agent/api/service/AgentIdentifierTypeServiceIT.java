package ca.gc.aafc.agent.api.service;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.agent.api.BaseIntegrationTest;
import ca.gc.aafc.agent.api.config.AgentVocabularyConfiguration;
import ca.gc.aafc.agent.api.entities.AgentIdentifierType;
import ca.gc.aafc.agent.api.testsupport.factories.AgentIdentifierTypeFactory;
import ca.gc.aafc.dina.vocabulary.VocabularyElementConfiguration;

import java.util.List;
import javax.inject.Inject;

public class AgentIdentifierTypeServiceIT extends BaseIntegrationTest {

  @Inject
  private AgentIdentifierTypeService agentIdentifierTypeService;

  @Inject
  private AgentVocabularyConfiguration agentVocabularyConfiguration;

  @Test
  public void createAgentIdentifierType() {
    var identifierType = AgentIdentifierTypeFactory.newAgentIdentifierType();
    agentIdentifierTypeService.create(identifierType.build());
  }

  @Test
  public void createAgentIdentifierType_fromVocabulary_recordCreated() {
    List<VocabularyElementConfiguration> identifiersVocabulary = agentVocabularyConfiguration
      .getVocabulary().get(AgentVocabularyConfiguration.IDENTIFIERS);
    VocabularyElementConfiguration element = identifiersVocabulary.getFirst();

    AgentIdentifierType identifierType = AgentIdentifierType.builder()
      .key(element.getKey())
      .name(element.getName())
      .term(element.getTerm())
      .multilingualTitle(element.getMultilingualTitle())
      .build();
    agentIdentifierTypeService.create(identifierType);
  }
}
