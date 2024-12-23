package ca.gc.aafc.agent.api.service;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.agent.api.BaseIntegrationTest;
import ca.gc.aafc.agent.api.testsupport.factories.AgentIdentifierTypeFactory;

import javax.inject.Inject;

public class AgentIdentifierTypeServiceIT extends BaseIntegrationTest {

  @Inject
  private AgentIdentifierTypeService agentIdentifierTypeService;

  @Test
  public void createAgentIdentifierType() {
    var identifierType = AgentIdentifierTypeFactory.newAgentIdentifierType();
    agentIdentifierTypeService.create(identifierType.build());
  }
}
