package ca.gc.aafc.agent.api.testsupport.factories;

import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;

import ca.gc.aafc.agent.api.entities.AgentIdentifierType;


public class AgentIdentifierTypeFactory {

  public static AgentIdentifierType.AgentIdentifierTypeBuilder<?,?> newAgentIdentifierType() {
    return AgentIdentifierType
      .builder()
      .uuid(UUID.randomUUID())
      .name(RandomStringUtils.randomAlphabetic(10));
  }
}
