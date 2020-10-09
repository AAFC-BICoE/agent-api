package ca.gc.aafc.agent.api;

import static org.junit.jupiter.api.Assertions.fail;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.transaction.Transactional;

import com.google.common.collect.ImmutableMap;

import org.apache.http.client.utils.URIBuilder;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import ca.gc.aafc.agent.api.entities.Person;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.DatabaseSupportService;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.specs.OpenAPI3Assertions;
import io.crnk.core.engine.http.HttpStatus;
import io.restassured.response.ValidatableResponse;

/**
 * Test suite to validate correct HTTP and JSON API responses for {@link Person}
 * Endpoints.
 */
@SpringBootTest(
  classes = AgentModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.yml")
@ContextConfiguration(initializers = { PostgresTestContainerInitializer.class })
@Transactional
public class PersonRestJsonIT extends BaseRestAssuredTest {

  @Inject
  private DatabaseSupportService databaseSupportService;

  private static URL specUrl;

  public static final String API_BASE_PATH = "/api/v1/person/";
  private static final String SPEC_HOST = "raw.githubusercontent.com";
  private static final String SPEC_PATH = "DINA-Web/agent-specs/master/schema/person.yml";
  private static final String SCHEMA_NAME = "Person";
  public static final String EMAIL_ERROR = "email must be a well-formed email address";

  private static final URIBuilder URI_BUILDER = new URIBuilder();
  static {
    URI_BUILDER.setScheme("https");
    URI_BUILDER.setHost(SPEC_HOST);
    URI_BUILDER.setPath(SPEC_PATH);
  }
  
  protected PersonRestJsonIT() {
    super(API_BASE_PATH);
    try {
      specUrl = URI_BUILDER.build().toURL();
    } catch (MalformedURLException | URISyntaxException e) {
      fail(e);
    }
  }

  @Test
  public void post_NewPerson_ReturnsOkayAndBody() {
    String displayName = "Albert";
    String email = "Albert@yahoo.com";

    ValidatableResponse response = super.sendPost("", getPostBody(displayName, email));

    assertValidResponseBodyAndCode(response, displayName, email, HttpStatus.CREATED_201)
      .body("data.id", Matchers.notNullValue());
    OpenAPI3Assertions.assertRemoteSchema(specUrl, SCHEMA_NAME, response.extract().asString());

    // Cleanup:
    UUID uuid = response.extract().jsonPath().getUUID("data.id");
    databaseSupportService.deleteByProperty(Person.class, "uuid", uuid);
  }

  @Test
  public void post_NewPerson_ReturnsInvalidEmail() {
    String displayName = "Albert";
    String email = "AlbertYahoo.com";

    super.sendPost("", getPostBody(displayName, email), HttpStatus.UNPROCESSABLE_ENTITY_422)
        .body("errors.detail", Matchers.hasItem(EMAIL_ERROR));
  }

  @Test
  public void Patch_UpdatePerson_ReturnsOkayAndBody() {
    String id = persistPerson("person", "person@agen.ca");

    String newName = "Updated Name";
    String newEmail = "Updated@yahoo.nz";
    super.sendPatch("", id, getPostBody(newName, newEmail));

    ValidatableResponse response = super.sendGet("", id);
    assertValidResponseBodyAndCode(response, newName, newEmail, HttpStatus.OK_200);
    OpenAPI3Assertions.assertRemoteSchema(specUrl, SCHEMA_NAME, response.extract().asString());

    // Cleanup:
    databaseSupportService.deleteByProperty(Person.class, "uuid", UUID.fromString(id));
  }

  @Test
  public void get_PersistedPerson_ReturnsOkayAndBody() {
    String displayName = TestableEntityFactory.generateRandomNameLettersOnly(10);
    String email = TestableEntityFactory.generateRandomNameLettersOnly(5) + "@email.com";
    String id = persistPerson(displayName, email);

    ValidatableResponse response = super.sendGet("", id);

    assertValidResponseBodyAndCode(response, displayName, email, HttpStatus.OK_200)
        .body("data.id", Matchers.equalTo(id));
    OpenAPI3Assertions.assertRemoteSchema(specUrl, SCHEMA_NAME, response.extract().asString());

    // Cleanup:
    databaseSupportService.deleteByProperty(Person.class, "uuid", UUID.fromString(id));
  }

  @Test
  public void get_InvalidPerson_ReturnsResourceNotFound() {
    super.sendGet("", "a8098c1a-f86e-11da-bd1a-00112444be1e", HttpStatus.NOT_FOUND_404);
  }

  @Test
  public void delete_PersistedPerson_ReturnsNoContentAndDeletes() {
    String id = persistPerson("person", "person@agen.ca");
    super.sendGet("", id);
    super.sendDelete("", id);
    super.sendGet("", id, HttpStatus.NOT_FOUND_404);

    // Cleanup:
    databaseSupportService.deleteByProperty(Person.class, "uuid", UUID.fromString(id));
  }

  /**
   * Assert a given response contains the correct name, email, and HTTP return
   * code as given.
   *
   * @param response
   *                        - response to validate
   * @param expectedName
   *                        - expected name in the response body
   * @param expectedEmail
   *                        - expected email in the response body
   * @param httpCode
   *                        - expected HTTP response code
   * @return - A validatable response from the request.
   */
  private static ValidatableResponse assertValidResponseBodyAndCode(ValidatableResponse response,
      String expectedName, String expectedEmail, int httpCode) {
    return response.statusCode(httpCode)
        .body("data.attributes.displayName", Matchers.equalTo(expectedName))
        .body("data.attributes.email", Matchers.equalTo(expectedEmail));
  }

  /**
   * Returns a serializable JSON API Map for use with POSTED request bodies.
   *
   * @param displayName
   *                      - name for the post body
   * @param email
   *                      - email for the post body
   * @return - serializable JSON API map
   */
  private static Map<String, Object> getPostBody(String displayName, String email) {
    ImmutableMap.Builder<String, Object> objAttribMap = new ImmutableMap.Builder<>();
    objAttribMap.put("displayName", displayName);
    objAttribMap.put("email", email);
    return JsonAPITestHelper.toJsonAPIMap("person", objAttribMap.build(), null, null);
  }

  /**
   * Helper method to persist an Person with a given name and email.
   *
   * @param name
   *                - name for the person
   * @param email
   *                - email for the person
   * @return - id of the persisted person
   */
  private String persistPerson(String name, String email) {
    return super.sendPost("", getPostBody(name, email)).extract().jsonPath().get("data.id");
  }

}
