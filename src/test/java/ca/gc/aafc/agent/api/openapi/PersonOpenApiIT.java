package ca.gc.aafc.agent.api.openapi;

import ca.gc.aafc.agent.api.dto.IdentifierDto;
import ca.gc.aafc.agent.api.dto.OrganizationDto;
import ca.gc.aafc.agent.api.dto.PersonDto;
import ca.gc.aafc.agent.api.entities.Identifier;
import ca.gc.aafc.agent.api.entities.Organization;
import ca.gc.aafc.agent.api.entities.Person;
import ca.gc.aafc.agent.api.testsupport.fixtures.IdentifierTestFixture;
import ca.gc.aafc.agent.api.testsupport.fixtures.OrganisationTestFixture;
import ca.gc.aafc.agent.api.testsupport.fixtures.PersonTestFixture;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocuments;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.DatabaseSupportService;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.specs.OpenAPI3Assertions;
import ca.gc.aafc.dina.testsupport.specs.ValidationRestrictionOptions;

import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;

import static ca.gc.aafc.agent.api.openapi.OpenAPIConstants.AGENT_API_SPECS_URL;

@TestPropertySource(properties = {
  "spring.config.additional-location=classpath:application-test.yml",
  "dev-user.enabled=true"})
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
@Transactional
public class PersonOpenApiIT extends BaseRestAssuredTest {

  public static final String API_BASE_PATH_PERSON = "/api/v1/person/";
  public static final String API_BASE_PATH_IDENTIFIER = "/api/v1/identifier/";
  public static final String API_BASE_PATH_ORGANIZATION = "/api/v1/organization/";
  private static final String SCHEMA_NAME = "Person";

  @Inject
  private DatabaseSupportService databaseSupportService;

  protected PersonOpenApiIT() {
    super(null);
  }

  @Test
  public void post_NewPerson_ReturnsOkayAndBody() throws JsonProcessingException {
    OrganizationDto organizationDto = OrganisationTestFixture.newOrganization();
    ValidatableResponse organizationResponse = sendPost(
      API_BASE_PATH_ORGANIZATION, 
      JsonAPITestHelper.toJsonAPIMap(
        "organization",
        JsonAPITestHelper.toAttributeMap(organizationDto),
        null,
        null
      )
    );
    String organizationUuid = JsonAPITestHelper.extractId(organizationResponse);

    IdentifierDto identifierDto = IdentifierTestFixture.newIdentifier();
    String identifierUUID = JsonAPITestHelper.extractId(sendPost(API_BASE_PATH_IDENTIFIER, JsonAPITestHelper
            .toJsonAPIMap(IdentifierDto.TYPENAME, JsonAPITestHelper.toAttributeMap(identifierDto))));

    PersonDto person = PersonTestFixture.newPerson();
    person.setIdentifiers(null);

    JsonApiDocument doc = JsonApiDocuments.createJsonApiDocumentWithRelToMany(
      null, PersonDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(person),
      Map.of("identifiers", List.of(JsonApiDocument.ResourceIdentifier.builder()
          .type(IdentifierDto.TYPENAME).id(UUID.fromString(identifierUUID)).build()),
        "organizations", List.of(JsonApiDocument.ResourceIdentifier.builder()
          .type(OrganizationDto.TYPENAME).id(UUID.fromString(organizationUuid)).build())
      )
    );

    ValidatableResponse response = super.sendPost(API_BASE_PATH_PERSON, doc);

    response
      .body("data.attributes.displayName", Matchers.equalTo(PersonTestFixture.DISPLAYNAME))
      .body("data.attributes.email", Matchers.equalTo(PersonTestFixture.EMAIL))
      .body("data.attributes.givenNames", Matchers.equalTo(PersonTestFixture.GIVENNAMES))
      .body("data.attributes.familyNames", Matchers.equalTo(PersonTestFixture.FAMILYNAMES))
      .body("data.id", Matchers.notNullValue());

    // allowAdditionalFields is only used for meta field
    OpenAPI3Assertions.assertRemoteSchema(AGENT_API_SPECS_URL, SCHEMA_NAME, response.extract().asString(),
      ValidationRestrictionOptions.builder().allowAdditionalFields(true).build());

    // Cleanup:
    UUID uuid = response.extract().jsonPath().getUUID("data.id");
    databaseSupportService.deleteByProperty(Identifier.class, "uuid", UUID.fromString(identifierUUID));
    databaseSupportService.deleteByProperty(Person.class, "uuid", uuid);
    databaseSupportService.deleteByProperty(Organization.class, "uuid", UUID.fromString(organizationUuid));
  }

}
