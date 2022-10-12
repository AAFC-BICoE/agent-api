package ca.gc.aafc.agent.api.openapi;

import ca.gc.aafc.agent.api.dto.PersonDto;
import ca.gc.aafc.agent.api.entities.Identifier;
import ca.gc.aafc.agent.api.entities.Organization;
import ca.gc.aafc.agent.api.entities.Person;
import ca.gc.aafc.agent.api.entities.Identifier.IdentifierType;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.DatabaseSupportService;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.specs.OpenAPI3Assertions;
import com.google.common.collect.ImmutableMap;
import io.restassured.response.ValidatableResponse;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@TestPropertySource(properties = {
  "spring.config.additional-location=classpath:application-test.yml",
  "dev-user.enabled=true"})
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
@Transactional
public class PersonOpenApiIT extends BaseRestAssuredTest {

  public static final String API_BASE_PATH_PERSON = "/api/v1/person/";
  public static final String API_BASE_PATH_ORGANIZATION = "/api/v1/organization/";
  private static final String SCHEMA_NAME = "Person";

  @Inject
  private DatabaseSupportService databaseSupportService;

  private static URL specUrl;

  @SneakyThrows({MalformedURLException.class, URISyntaxException.class})
  protected PersonOpenApiIT() {
    super(null);
    specUrl = createSchemaUriBuilder(OpenAPIConstants.SPEC_HOST, OpenAPIConstants.SPEC_PATH).build()
        .toURL();
  }

  @Test
  public void post_NewOrganization_ReturnsOkayAndBody() {
    String email = "test@canada.ca";
    String displayName = "test user";
    String givenNames = "Jane";
    String familyNames = "Doe";
    List<String> aliases = List.of("alias1", "alias2");
    String webpage = "https://github.com/DINA-Web";
    String remarks = "this is a mock remark";

    Identifier identifier = Identifier.builder()
      .type(IdentifierType.WIKIDATA)
      .uri("https://www.wikidata.org/wiki/Q51044")
      .build();
    
    List<Identifier> identifiers = Collections.singletonList(identifier);

    ValidatableResponse organizationResponse = sendPost(
      API_BASE_PATH_ORGANIZATION, 
      JsonAPITestHelper.toJsonAPIMap(
        "organization",
        new ImmutableMap.Builder<String, Object>()
          .put("aliases", aliases)
          .put("names", Collections.singletonList(ImmutableMap.of(
            "languageCode", "te",
            "name", "test")))
          .build(),
        null,
        null
      )
    );
    UUID organizationUuid = organizationResponse.extract().jsonPath().getUUID("data.id");

    PersonDto person = new PersonDto();

    person.setEmail(email);
    person.setDisplayName(displayName);
    person.setGivenNames(givenNames);
    person.setFamilyNames(familyNames);
    person.setIdentifiers(identifiers);
    person.setWebpage(webpage);
    person.setRemarks(remarks);
    
    ValidatableResponse response = super.sendPost(
      API_BASE_PATH_PERSON,
      JsonAPITestHelper.toJsonAPIMap(
        "person",
        JsonAPITestHelper.toAttributeMap(person),
        Map.of(
          "organizations", getRelationshipListType("organization", organizationUuid.toString())),
        null
      )
    );

    response
      .body("data.attributes.displayName", Matchers.equalTo(displayName))
      .body("data.attributes.email", Matchers.equalTo(email))
      .body("data.attributes.givenNames", Matchers.equalTo(givenNames))
      .body("data.attributes.familyNames", Matchers.equalTo(familyNames))
      .body("data.id", Matchers.notNullValue());
    OpenAPI3Assertions.assertRemoteSchema(specUrl, SCHEMA_NAME, response.extract().asString());

    // Cleanup:
    UUID uuid = response.extract().jsonPath().getUUID("data.id");
    databaseSupportService.deleteByProperty(Person.class, "uuid", uuid);
    databaseSupportService.deleteByProperty(Organization.class, "uuid", organizationUuid);
  }

  private Map<String, Object> getRelationshipListType(String type, String uuid) {
    return Map.of("data", List.of(Map.of(
      "id", uuid,
      "type", type)));
  }

}
