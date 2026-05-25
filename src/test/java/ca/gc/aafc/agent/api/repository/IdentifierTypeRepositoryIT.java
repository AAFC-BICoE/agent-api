package ca.gc.aafc.agent.api.repository;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import ca.gc.aafc.agent.api.BaseIntegrationTest;
import ca.gc.aafc.agent.api.dto.AgentIdentifierTypeDto;
import ca.gc.aafc.agent.api.dto.PersonDto;
import ca.gc.aafc.agent.api.testsupport.fixtures.AgentIdentifierTypeTestFixture;
import ca.gc.aafc.dina.exception.ResourceGoneException;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocuments;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import ca.gc.aafc.dina.util.UUIDHelper;

@SpringBootTest(properties = {"keycloak.enabled: true"})
public class IdentifierTypeRepositoryIT extends BaseIntegrationTest {

  @Inject
  private AgentIdentifierTypeRepository agentIdentifierTypeRepository;

  @Test
  @WithMockKeycloakUser(username = "user", adminRole = {"DINA_ADMIN"})
  public void agentIdentifierTypeRepository_onCreate_noException()
      throws ResourceGoneException, ResourceNotFoundException {
    UUID uuid = UUIDHelper.generateUUIDv7();

    AgentIdentifierTypeDto dto = AgentIdentifierTypeTestFixture.newAgentIdentifierType();
    dto.setUuid(uuid);

    JsonApiDocument docToCreate = JsonApiDocuments.createJsonApiDocument(
      uuid, PersonDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(dto)
    );

    agentIdentifierTypeRepository.onCreate(docToCreate);

    //cleanup
    agentIdentifierTypeRepository.onDelete(uuid);
  }
}
