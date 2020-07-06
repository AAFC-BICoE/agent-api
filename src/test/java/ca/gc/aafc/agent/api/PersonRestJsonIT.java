package ca.gc.aafc.agent.api;

import static io.restassured.RestAssured.given;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.http.client.utils.URIBuilder;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.google.common.collect.ImmutableMap;

import ca.gc.aafc.agent.api.entities.Person;
import ca.gc.aafc.dina.testsupport.DBBackedIntegrationTest;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.specs.OpenAPI3Assertions;
import io.crnk.core.engine.http.HttpStatus;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import lombok.extern.log4j.Log4j2;

/**
 * Test suite to validate correct HTTP and JSON API responses for {@link Person}
 * Endpoints.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@Log4j2
public class PersonRestJsonIT extends DBBackedIntegrationTest {

  @LocalServerPort
  protected int testPort;

  public static final String API_BASE_PATH = "/api/v1/person/";
  public static final String JSON_API_CONTENT_TYPE = "application/vnd.api+json";  
  private static final String SPEC_HOST = "raw.githubusercontent.com";
  private static final String SPEC_PATH = "DINA-Web/agent-specs/master/schema/agent.yml";  
  private static final String SCHEMA_NAME = "Person";

  @BeforeEach
  public void setup() {
    RestAssured.port = testPort;
  }

  /**
   * Remove database entries after each test.
   */
  @AfterEach
  public void tearDown() {
    runInNewTransaction(em -> {
      CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
      CriteriaDelete<Person> query = criteriaBuilder.createCriteriaDelete(Person.class);
      Root<Person> root = query.from(Person.class);
      query.where(criteriaBuilder.isNotNull(root.get("uuid")));
      em.createQuery(query).executeUpdate();
    });
  }

  @Test
  public void post_NewPerson_ReturnsOkayAndBody() {
    String displayName = "Albert";
    String email = "Albert@yahoo.com";

    Response response = postPerson(displayName, email);

    assertValidResponseBodyAndCode(response, displayName, email, HttpStatus.CREATED_201)
      .body("data.id", Matchers.notNullValue());
    validateJsonSchema(response.body().asString());
  }

  @Test
  public void Patch_UpdatePerson_ReturnsOkayAndBody() {
    String id = persistPerson("person", "person@agen.ca");

    String newName = "Updated Name";
    String newEmail = "Updated@yahoo.nz";
    patchPerson(newName, newEmail, id);

    Response response = sendGet(id);
    assertValidResponseBodyAndCode(response, newName, newEmail, HttpStatus.OK_200);
    validateJsonSchema(response.body().asString());
  }

  @Test
  public void get_PersistedPerson_ReturnsOkayAndBody() {
    String displayName = TestableEntityFactory.generateRandomNameLettersOnly(10);
    String email = TestableEntityFactory.generateRandomNameLettersOnly(5);
    String id = persistPerson(displayName, email);

    Response response = sendGet(id);

    assertValidResponseBodyAndCode(
        response,
        displayName,
        email,
        HttpStatus.OK_200
      ).body("data.id", Matchers.equalTo(id));
    validateJsonSchema(response.body().asString());
  }

  @Test
  public void get_InvalidPerson_ReturnsResourceNotFound() {
    Response response = sendGet("a8098c1a-f86e-11da-bd1a-00112444be1e");
    response.then().statusCode(HttpStatus.NOT_FOUND_404);
  }

  @Test
  public void get_InvalidUUID_ReturnsBadRequest() {
    Response getResponse = sendGet("12131231");
    getResponse.then().statusCode(HttpStatus.BAD_REQUEST_400);

    Response getWithQueryString = sendGet("12131231"+
    "?filter[acDerivedFrom.id][EQ]=a8098c1a-f86e-11da-bd1a-00112444be1e");
    getWithQueryString.then().statusCode(HttpStatus.BAD_REQUEST_400);
  }

  @Test
  public void delete_PeresistedPerson_ReturnsNoConentAndDeletes() {
    String id = persistPerson("person", "person@agen.ca");

    Response response = sendDelete(id);
    response.then().statusCode(HttpStatus.NO_CONTENT_204);

    Response getResponse = sendGet(id);
    getResponse.then().statusCode(HttpStatus.NOT_FOUND_404);
  }

  /**
   * Send a HTTP DELETE request to the person endpoint with a given id
   *
   * @param id - id of the entity
   * @return - response of the request
   */
  private Response sendDelete(String id) {
    return given()
      .header("crnk-compact", "true")
      .when()
      .delete(API_BASE_PATH + id);
  }

  /**
   * Send a HTTP GET request to the person endpoint with a given id
   *
   * @param id - id of the entity
   * @return - response of the request
   */
  private Response sendGet(String id) {
    return given()
      .header("crnk-compact", "true")
      .when()
      .get(API_BASE_PATH + id);
  }

  /**
   * Send a HTTP PATCH request to the person endpoint with a given id, name, and
   * email.
   *
   * @param newDisplayName - new name for the person
   * @param newEmail       - new email for the person
   * @param id             - id of the entity
   * @return - response of the request
   */
  private Response patchPerson(String newDisplayName, String newEmail, String id) {
    return given()
      .header("crnk-compact", "true")
      .contentType(JSON_API_CONTENT_TYPE)
      .body(getPostBody(newDisplayName, newEmail))
      .when()
      .patch(API_BASE_PATH + id);
  }

  /**
   * Send a HTTP POST request to the person endpoint with a given name and email.
   *
   * @param displayName - name for the person
   * @param email       - email for the person
   * @return - response of the request
   */
  private Response postPerson(String displayName, String email) {
    return given()
      .header("crnk-compact", "true")
      .contentType(JSON_API_CONTENT_TYPE)
      .body(getPostBody(displayName, email))
      .when()
      .post(API_BASE_PATH);
  }

  /**
   * Assert a given response contains the correct name, email, and HTTP return
   * code as given.
   *
   * @param response      - response to validate
   * @param expectedName  - expected name in the response body
   * @param expectedEmail - expected email in the response body
   * @param httpCode      - expected HTTP response code
   * @return - A validatable response from the request.
   */
  private static ValidatableResponse assertValidResponseBodyAndCode(
      Response response,
      String expectedName,
      String expectedEmail,
      int httpCode
  ) {
    return response.then()
      .statusCode(httpCode)
      .body("data.attributes.displayName", Matchers.equalTo(expectedName))
      .body("data.attributes.email", Matchers.equalTo(expectedEmail));
  }

  /**
   * Returns a serializable JSON API Map for use with POSTED request bodies.
   *
   * @param displayName - name for the post body
   * @param email       - email for the post body
   * @return - serializable JSON API map
   */
  private static Map<String, Object> getPostBody(String displayName, String email) {
    ImmutableMap.Builder<String, Object> objAttribMap = new ImmutableMap.Builder<>();
    objAttribMap.put("displayName", displayName);
    objAttribMap.put("email", email);
    return JsonAPITestHelper.toJsonAPIMap("person", objAttribMap.build(),null, null);
  }

  /**
   * Helper method to persist an Person with a given name and email.
   *
   * @param name  - name for the person
   * @param email - email for the person
   * @return - id of the persisted person
   */
  private String persistPerson(String name, String email) {
    String id =  postPerson(name, email)
      .body()
      .jsonPath()
      .get("data.id");
    return id;
  }

  /**
   * Validates a given JSON response body matches the schema defined in
   * {@link PersonRestJsonIT#SCHEMA_NAME}
   *
   * @param responseJson The response json from service
   */
  private void validateJsonSchema(String responseJson) {
    if (!Boolean.valueOf(System.getProperty("testing.skip-external-schema-validation"))) {
      try {
        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setScheme("https");
        uriBuilder.setHost(SPEC_HOST);
        uriBuilder.setPath(SPEC_PATH);
        log.info(
          "Validating {} schema against the following response: {}",
          () -> SCHEMA_NAME,
          () -> responseJson);
        OpenAPI3Assertions.assertSchema(uriBuilder.build().toURL(), SCHEMA_NAME, responseJson);
      } catch (URISyntaxException | MalformedURLException e) {
        log.error(e);
      }
    } else {
      log.warn(
        "Skipping schema validation." + 
        "System property testing.skip-external-schema-validation set to true. "
        );
    }
  }
}