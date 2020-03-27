package ca.gc.aafc.agent.api;

import static io.restassured.RestAssured.given;

import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import com.google.common.collect.ImmutableMap;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import ca.gc.aafc.agent.api.entities.Agent;
import ca.gc.aafc.agent.api.utils.TestUtils;
import ca.gc.aafc.dina.testsupport.DBBackedIntegrationTest;
import io.crnk.core.engine.http.HttpStatus;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@ActiveProfiles("test")
public class AgentRestJsonIT extends DBBackedIntegrationTest {

  @LocalServerPort
  protected int testPort;

  public static final String API_BASE_PATH = "/api/v1/agent/";

  public static final String JSON_API_CONTENT_TYPE = "application/vnd.api+json";

  @BeforeEach
  public void setup() {
    RestAssured.port = testPort;
  }

  @AfterEach
  public void tearDown() {
    runInNewTransaction(em -> {
      CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
      CriteriaDelete<Agent> query = criteriaBuilder.createCriteriaDelete(Agent.class);
      Root<Agent> root = query.from(Agent.class);
      query.where(criteriaBuilder.isNotNull(root.get("uuid")));
      em.createQuery(query).executeUpdate();
    });
  }

  @Test
  public void post_NewAgent_ReturnsOkayAndBody() {
    String displayName = "Albert";
    String email = "Albert@yahoo.com";

    Response response = postAgent(displayName, email);

    assertValidResponseBodyAndCode(response, displayName, email, HttpStatus.CREATED_201)
      .body("data.id", Matchers.notNullValue());
  }

  @Test
  public void Patch_UpdateAgent_ReturnsOkayAndBody() {
    Agent persistedAgent = TestUtils.generateAgent();
    String id =  postAgent(persistedAgent.getDisplayName(), persistedAgent.getEmail())
      .body()
      .jsonPath()
      .get("data.id");

    String newName="Updated Name";
    String newEmail = "Updated@yahoo.nz";

    patchAgent(newName,newEmail,id);

    Response response = sendGet(id);

    assertValidResponseBodyAndCode(response, newName, newEmail, HttpStatus.OK_200);
  }

  @Test
  public void get_PersistedAgent_ReturnsOkayAndBody() {
    Agent persistedAgent = TestUtils.generateAgent();
    String id =  postAgent(persistedAgent.getDisplayName(), persistedAgent.getEmail())
      .body()
      .jsonPath()
      .get("data.id");

    Response response = sendGet(id);

    assertValidResponseBodyAndCode(
        response,
        persistedAgent.getDisplayName(),
        persistedAgent.getEmail(),
        HttpStatus.OK_200
      ).body("data.id", Matchers.equalTo(id));
  }

  @Test
  public void get_InvalidAgent_ReturnsResourceNotFound() {
    Response response = sendGet("a8098c1a-f86e-11da-bd1a-00112444be1e");
    response.then().statusCode(HttpStatus.NOT_FOUND_404);
  }

  @Test
  public void delete_PeresistedAgent_ReturnsNoConentAndDeletes() {
    Agent persistedAgent = TestUtils.generateAgent();
    String id =  postAgent(persistedAgent.getDisplayName(), persistedAgent.getEmail())
      .body()
      .jsonPath()
      .get("data.id");

    Response response = sendDelete(id);
    response.then().statusCode(HttpStatus.NO_CONTENT_204);

    Response getResponse = sendGet(id);
    getResponse.then().statusCode(HttpStatus.NOT_FOUND_404);
  }

  private Response sendDelete(String id) {
    return given()
      .header("crnk-compact", "true")
      .when()
      .delete(API_BASE_PATH + id);
  }

  private Response sendGet(String id) {
    return given()
      .header("crnk-compact", "true")
      .when()
      .get(API_BASE_PATH + id);
  }

  private Response patchAgent(String newDisplayName, String newEmail, String id) {
    return given()
      .header("crnk-compact", "true")
      .contentType(JSON_API_CONTENT_TYPE)
      .body(getPostBody(newDisplayName, newEmail))
      .when()
      .patch(API_BASE_PATH + id);
  }

  private Response postAgent(String displayName, String email) {
    return given()
      .header("crnk-compact", "true")
      .contentType(JSON_API_CONTENT_TYPE)
      .body(getPostBody(displayName, email))
      .when()
      .post(API_BASE_PATH);
  }

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

  private static Map<String, Object> getPostBody(String displayName, String email) {
    ImmutableMap.Builder<String, Object> objAttribMap = new ImmutableMap.Builder<>();
    objAttribMap.put("displayName", displayName);
    objAttribMap.put("email", email);
    return TestUtils.toJsonAPIMap("agent", objAttribMap.build(), null);
  }

}