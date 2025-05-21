package ca.gc.aafc.agent.api;

import ca.gc.aafc.agent.api.entities.Person;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.DatabaseSupportService;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import com.google.common.collect.ImmutableMap;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Test suite to validate correct HTTP and JSON API responses for {@link Person}
 * Endpoints.
 */
@SpringBootTest(
  classes = AgentModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource(properties = {
  "spring.config.additional-location=classpath:application-test.yml",
  "dev-user.enabled=true"})
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
@Transactional
public class PersonRestJsonIT extends BaseRestAssuredTest {

  @Inject
  private DatabaseSupportService databaseSupportService;

  public static final String API_BASE_PATH = "/api/v1/person/";
  private static final String SCHEMA_NAME = "Person";
  public static final String EMAIL_ERROR = "email must be a well-formed email address";

  protected PersonRestJsonIT() {
    super(API_BASE_PATH);
  }

  @Test
  public void post_NewPerson_ReturnsOkayAndBody() {
    String displayName = "Albert";
    String email = "Albert@yahoo.com";
    List<String> aliases = List.of("dina user");

    ValidatableResponse response = super.sendPost("", getPostBody(displayName, email, aliases, null));

    assertValidResponseBodyAndCode(response, displayName, email, aliases, HttpStatus.CREATED.value())
      .body("data.id", Matchers.notNullValue());

    // Cleanup:
    UUID uuid = response.extract().jsonPath().getUUID("data.id");
    databaseSupportService.deleteByProperty(Person.class, "uuid", uuid);
  }

  @Test
  public void post_NewPerson_ReturnsInvalidEmail() {
    String displayName = "Albert";
    String email = "AlbertYahoo.com";

    super.sendPost("", getPostBody(displayName, email, List.of("dina user"), null), HttpStatus.UNPROCESSABLE_ENTITY.value())
        .body("errors.detail", Matchers.hasItem(EMAIL_ERROR));
  }

  @Test
  public void patch_UpdatePerson_ReturnsOkayAndBody() {
    String id = persistPerson("person", "person@agen.ca");

    String newName = "Updated Name";
    String newEmail = "Updated@yahoo.nz";
    List<String> newAliases = List.of("dina user");
    super.sendPatch("", id, getPostBody(newName, newEmail, newAliases, id));

    ValidatableResponse response = super.sendGet("", id);
    assertValidResponseBodyAndCode(response, newName, newEmail, newAliases, HttpStatus.OK.value());

    // Cleanup:
    databaseSupportService.deleteByProperty(Person.class, "uuid", UUID.fromString(id));
  }

  @Test
  public void get_PersistedPerson_ReturnsOkayAndBody() {
    String displayName = TestableEntityFactory.generateRandomNameLettersOnly(10);
    String email = TestableEntityFactory.generateRandomNameLettersOnly(5) + "@email.com";
    String id = persistPerson(displayName, email);

    ValidatableResponse response = super.sendGet("", id);

    assertValidResponseBodyAndCode(response, displayName, email, List.of("dina user"), HttpStatus.OK.value())
        .body("data.id", Matchers.equalTo(id));

    // Cleanup:
    databaseSupportService.deleteByProperty(Person.class, "uuid", UUID.fromString(id));
  }

  @Test
  public void get_InvalidPerson_ReturnsResourceNotFound() {
    super.sendGet("", "a8098c1a-f86e-11da-bd1a-00112444be1e", HttpStatus.NOT_FOUND.value());
  }

  @Test
  public void delete_PersistedPerson_ReturnsNoContentAndDeletes() {
    String id = persistPerson("person", "person@agen.ca");
    super.sendGet("", id);
    super.sendDelete("", id);
    super.sendGet("", id,  410);

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
      String expectedName, String expectedEmail,List<String> aliases, int httpCode) {
    return response.statusCode(httpCode)
        .body("data.attributes.displayName", Matchers.equalTo(expectedName))
        .body("data.attributes.email", Matchers.equalTo(expectedEmail))
        .body("data.attributes.aliases", Matchers.containsInAnyOrder(aliases.toArray()));
  }

  /**
   * Returns a serializable JSON API Map for use with POSTED request bodies.
   *
   * @param displayName
   *                      - name for the post body
   * @param email
   *                      - email for the post body
   * @param aliases
   * @return - serializable JSON API map
   */
  private static Map<String, Object> getPostBody(String displayName, String email, List<String> aliases, String id) {
    ImmutableMap.Builder<String, Object> objAttribMap = new ImmutableMap.Builder<>();
    objAttribMap.put("displayName", displayName);
    objAttribMap.put("email", email);
    objAttribMap.put("aliases", aliases);
    return JsonAPITestHelper.toJsonAPIMap("person", objAttribMap.build(), null, id);
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
    return super.sendPost("", getPostBody(name, email, List.of("dina user"), null)).extract().jsonPath().get("data.id");
  }

}
