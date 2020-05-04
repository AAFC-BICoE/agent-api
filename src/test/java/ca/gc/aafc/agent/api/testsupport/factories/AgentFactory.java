package ca.gc.aafc.agent.api.testsupport.factories;

import java.util.UUID;

import ca.gc.aafc.agent.api.entities.Agent;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;

public class AgentFactory implements TestableEntityFactory<Agent> {

  @Override
  public Agent getEntityInstance() {
    return newAgent().build();
  }

  /**
   * Static method that can be called to return a configured builder that can be
   * further customized to return the actual entity object, call the .build()
   * method on a builder.
   * 
   * @return Pre-configured builder with all mandatory fields set
   */
  public static Agent.AgentBuilder newAgent() {
    return Agent
      .builder()
      .uuid(UUID.randomUUID())
      .displayName(TestableEntityFactory.generateRandomNameLettersOnly(15))
      .email(TestableEntityFactory.generateRandomNameLettersOnly(5) + "@email.com");
  }

}

