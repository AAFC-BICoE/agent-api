package ca.gc.aafc.agent.api.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.gc.aafc.agent.api.config.AgentControlledVocabularyConfiguration;
import ca.gc.aafc.agent.api.dto.AgentControlledVocabularyDto;
import ca.gc.aafc.agent.api.dto.AgentControlledVocabularyItemDto;
import ca.gc.aafc.agent.api.testsupport.fixtures.AgentControlledVocabularyItemTestFixture;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocuments;
import ca.gc.aafc.dina.repository.JsonApiModelAssistant;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import ca.gc.aafc.dina.vocabulary.TypedVocabularyElement;
import jakarta.inject.Inject;

@SpringBootTest(properties = "keycloak.enabled=true")
public class AgentControlledVocabularyItemRepositoryIT extends AgentModuleBaseRepositoryIT {

  private static final String BASE_URL = "/api/v1/" + AgentControlledVocabularyItemDto.TYPENAME;

  @Autowired
  private WebApplicationContext wac;

  private MockMvc mockMvc;

  @Inject
  private AgentControlledVocabularyItemRepository repo;

  @Autowired
  public AgentControlledVocabularyItemRepositoryIT(ObjectMapper objMapper) {
    super(BASE_URL, objMapper);
  }

  @Override
  protected MockMvc getMockMvc() {
    return mockMvc;
  }

  @BeforeEach
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
  }

  @Test
  @WithMockKeycloakUser(groupRole = "dina-group:DINA_ADMIN", adminRole = "DINA_ADMIN")
  void create_recordCreated() throws Exception {
    String expectedName = "dina attribute #12";
    String expectedValue = "dina value";
    String expectedCreatedBy = "dina";
    String expectedGroup = "dina-group";

    AgentControlledVocabularyItemDto dto =
      AgentControlledVocabularyItemTestFixture.newAgentControlledVocabularyItem();

    dto.setName(expectedName);
    dto.setVocabularyElementType(TypedVocabularyElement.VocabularyElementType.STRING);
    dto.setAcceptedValues(new String[]{expectedValue});
    dto.setDinaComponent(AgentControlledVocabularyConfiguration.DinaComponent.PERSON.name());
    dto.setCreatedBy(expectedCreatedBy);
    dto.setGroup(expectedGroup);

    JsonApiDocument docToCreate = JsonApiDocuments.createJsonApiDocumentWithRelToOne(
      null, AgentControlledVocabularyItemDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(dto),
      Map.of("controlledVocabulary", JsonApiDocument.ResourceIdentifier.builder()
        .type(AgentControlledVocabularyDto.TYPENAME)
        .id(AgentControlledVocabularyConfiguration.IDENTIFIER_TYPE_VOCAB_UUID).build()
      )
    );

    var created = repo.onCreate(docToCreate);
    UUID uuid = JsonApiModelAssistant.extractUUIDFromRepresentationModelLink(created);

    // try get by uuid
    sendGet(uuid.toString());

    // try get be key
    sendGet("identifier_type.dina_attribute_12.PERSON");
  }

  @Test
  @WithMockKeycloakUser(groupRole = AgentControlledVocabularyItemTestFixture.GROUP + ":SUPER_USER")
  void findOneByKey_whenBadKeyProvided_responseSanitized() throws Exception {
    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
      () -> repo.onFindOne("managed_attribute.attr_1<iframe src=javascript:alert(24109)", null));

    assertFalse(exception.getMessage().contains("alert(24109)"));
  }
}
