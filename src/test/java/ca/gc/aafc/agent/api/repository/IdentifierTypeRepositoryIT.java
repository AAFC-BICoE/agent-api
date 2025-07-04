package ca.gc.aafc.agent.api.repository;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import ca.gc.aafc.agent.api.BaseIntegrationTest;
import ca.gc.aafc.agent.api.dto.AgentIdentifierTypeDto;
import ca.gc.aafc.agent.api.dto.PersonDto;
import ca.gc.aafc.agent.api.testsupport.fixtures.AgentIdentifierTypeTestFixture;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocuments;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

@SpringBootTest(properties = {"keycloak.enabled: true"})
public class IdentifierTypeRepositoryIT extends BaseIntegrationTest {

  @Inject
  private AgentIdentifierTypeRepository agentIdentifierTypeRepository;

  @Test
  @WithMockKeycloakUser(username = "user", adminRole = {"DINA_ADMIN"})
  public void agentIdentifierTypeRepository_onCreate_noException() {
    AgentIdentifierTypeDto dto = AgentIdentifierTypeTestFixture.newAgentIdentifierType();

    JsonApiDocument docToCreate = JsonApiDocuments.createJsonApiDocument(
      null, PersonDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(dto)
    );

    agentIdentifierTypeRepository.create(docToCreate, null);
  }
}
