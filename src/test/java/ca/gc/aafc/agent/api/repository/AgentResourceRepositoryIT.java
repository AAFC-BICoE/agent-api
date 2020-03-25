package ca.gc.aafc.agent.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import ca.gc.aafc.agent.api.dto.AgentDto;
import ca.gc.aafc.agent.api.entities.Agent;
import ca.gc.aafc.dina.testsupport.DBBackedIntegrationTest;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;
import io.crnk.core.queryspec.QuerySpec;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class AgentResourceRepositoryIT extends DBBackedIntegrationTest {

  @Inject
  private AgentResourceRepository agentResourceRepository;

  private Agent agentUnderTest;

  @BeforeEach
  public void setup() {
    agentUnderTest = generateAgent();
    save(agentUnderTest);
  }

  @Test
  public void create_ValidAgent_AgentPersisted() {
    AgentDto agentDto = new AgentDto();
    agentDto.setDisplayName(TestableEntityFactory.generateRandomNameLettersOnly(10));
    agentDto.setEmail(TestableEntityFactory.generateRandomNameLettersOnly(5));

    UUID uuid = agentResourceRepository.create(agentDto).getUuid();

    Agent result = findUnique(Agent.class, "uuid", uuid);
    assertEquals(agentDto.getDisplayName(), result.getDisplayName());
    assertEquals(agentDto.getEmail(), result.getEmail());
    assertEquals(uuid, result.getUuid());
  }

  @Test
  public void save_PersistedAgent_FieldsUpdated() {
    String updatedEmail = "Updated_Email";
    String updatedName = "Updated_Name";

    AgentDto updatedAgent = agentResourceRepository.findOne(
      agentUnderTest.getUuid(),
      new QuerySpec(AgentDto.class)
    );
    updatedAgent.setDisplayName(updatedName);
    updatedAgent.setEmail(updatedEmail);

    agentResourceRepository.save(updatedAgent);

    Agent result = findUnique(Agent.class, "uuid", updatedAgent.getUuid());
    assertEquals(updatedName, result.getDisplayName());
    assertEquals(updatedEmail, result.getEmail());
  }

  @Test
  public void find_NoFieldsSelected_ReturnsAllFields() {
    AgentDto result = agentResourceRepository.findOne(
      agentUnderTest.getUuid(),
      new QuerySpec(AgentDto.class)
    );

    assertEquals(agentUnderTest.getDisplayName(), result.getDisplayName());
    assertEquals(agentUnderTest.getEmail(), result.getEmail());
    assertEquals(agentUnderTest.getUuid(), result.getUuid());
  }

  @Test
  public void remove_PersistedAgent_AgentRemoved() {
    AgentDto persistedAgent = agentResourceRepository.findOne(
      agentUnderTest.getUuid(),
      new QuerySpec(AgentDto.class)
    );

    assertNotNull(find(Agent.class, agentUnderTest.getId()));
    agentResourceRepository.delete(persistedAgent.getUuid());
    assertNull(find(Agent.class, agentUnderTest.getId()));
  }

  private static Agent generateAgent() {
    return Agent.builder()
      .displayName(TestableEntityFactory.generateRandomNameLettersOnly(10))
      .uuid(UUID.randomUUID())
      .email(TestableEntityFactory.generateRandomNameLettersOnly(5))
      .build();
  }
}