package ca.gc.aafc.agent.api.openapi;

import ca.gc.aafc.agent.api.dto.IdentifierDto;
import ca.gc.aafc.agent.api.dto.PersonDto;
import ca.gc.aafc.agent.api.entities.Identifier;
import ca.gc.aafc.agent.api.entities.Organization;
import ca.gc.aafc.agent.api.entities.Person;
import ca.gc.aafc.agent.api.testsupport.fixtures.IdentifierTestFixture;
import ca.gc.aafc.agent.api.testsupport.fixtures.PersonTestFixture;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.DatabaseSupportService;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPIRelationship;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.specs.OpenAPI3Assertions;
import com.google.common.collect.ImmutableMap;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
  public void post_NewPerson_ReturnsOkayAndBody() {

    ValidatableResponse organizationResponse = sendPost(
      API_BASE_PATH_ORGANIZATION, 
      JsonAPITestHelper.toJsonAPIMap(
        "organization",
        new ImmutableMap.Builder<String, Object>()
          .put("aliases", List.of("alias1", "alias2"))
          .put("names", Collections.singletonList(ImmutableMap.of(
            "languageCode", "te",
            "name", "test")))
          .build(),
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
    
    ValidatableResponse response = super.sendPost(
      API_BASE_PATH_PERSON,
      JsonAPITestHelper.toJsonAPIMap(
        "person",
        JsonAPITestHelper.toAttributeMap(person),
        JsonAPITestHelper.toRelationshipMap( List.of(
                JsonAPIRelationship.of("organizations", "organization", organizationUuid),
                JsonAPIRelationship.of("identifiers", "identifier", identifierUUID))),
        null
      )
    );

    response
      .body("data.attributes.displayName", Matchers.equalTo(PersonTestFixture.displayName))
      .body("data.attributes.email", Matchers.equalTo(PersonTestFixture.email))
      .body("data.attributes.givenNames", Matchers.equalTo(PersonTestFixture.givenNames))
      .body("data.attributes.familyNames", Matchers.equalTo(PersonTestFixture.familyNames))
      .body("data.id", Matchers.notNullValue());
    OpenAPI3Assertions.assertRemoteSchema(AGENT_API_SPECS_URL, SCHEMA_NAME, response.extract().asString());

    // Cleanup:
    UUID uuid = response.extract().jsonPath().getUUID("data.id");
    databaseSupportService.deleteByProperty(Identifier.class, "uuid", UUID.fromString(identifierUUID));
    databaseSupportService.deleteByProperty(Person.class, "uuid", uuid);
    databaseSupportService.deleteByProperty(Organization.class, "uuid", UUID.fromString(organizationUuid));
  }

}
