package ca.gc.aafc.agent.api.testsupport.fixtures;

import ca.gc.aafc.agent.api.dto.AgentIdentifierTypeDto;

public class AgentIdentifierTypeTestFixture {

  public static AgentIdentifierTypeDto newAgentIdentifierType() {
    AgentIdentifierTypeDto identifierTypeDto = new AgentIdentifierTypeDto();
    identifierTypeDto.setName("ORCID");
    return identifierTypeDto;
  }
}
