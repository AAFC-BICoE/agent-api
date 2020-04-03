package ca.gc.aafc.agent.api.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import ca.gc.aafc.agent.api.testsupport.factories.AgentFactory;
import ca.gc.aafc.dina.testsupport.DBBackedIntegrationTest;

/**
 * Test suite to validate {@link Agent} performs as a valid Hibernate Entity.
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class AgentCrudIT extends DBBackedIntegrationTest {

  private Agent agentUnderTest;

  @BeforeEach
  public void setup() {
    agentUnderTest = AgentFactory.newAgent().build();
    save(agentUnderTest);
  }

  @Test
  public void testSave() {
    Agent agent = AgentFactory.newAgent().build();
    assertNull(agent.getId());
    save(agent);
    assertNotNull(agent.getId());
  }

  @Test
  public void testFind() {
    Agent fetchedAgent = find(Agent.class, agentUnderTest.getId());
    assertEquals(agentUnderTest.getId(), fetchedAgent.getId());
    assertEquals(agentUnderTest.getDisplayName(), fetchedAgent.getDisplayName());
    assertEquals(agentUnderTest.getEmail(), fetchedAgent.getEmail());
    assertEquals(agentUnderTest.getUuid(), fetchedAgent.getUuid());
  }

  @Test
  public void testRemove() {
    Integer id = agentUnderTest.getId();
    remove(Agent.class, id);
    assertNull(find(Agent.class, id));
  }

}
