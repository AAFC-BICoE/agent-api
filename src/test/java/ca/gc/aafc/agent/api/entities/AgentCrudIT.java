package ca.gc.aafc.agent.api.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import ca.gc.aafc.dina.testsupport.DBBackedIntegrationTest;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class AgentCrudIT extends DBBackedIntegrationTest {

  private Agent agentUnderTest;

  @BeforeEach
  public void setup() {
    agentUnderTest = generateAgent();
    save(agentUnderTest);
  }

  @Test
  public void testSave() {
    Agent agent = generateAgent();
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

  private static Agent generateAgent() {
    return Agent.builder()
      .displayName(TestableEntityFactory.generateRandomNameLettersOnly(10))
      .uuid(UUID.randomUUID())
      .email(TestableEntityFactory.generateRandomNameLettersOnly(5))
      .build();
  }
}