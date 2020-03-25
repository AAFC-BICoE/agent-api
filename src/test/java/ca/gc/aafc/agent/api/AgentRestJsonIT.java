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

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@ActiveProfiles("test")
public class AgentRestJsonIT extends DBBackedIntegrationTest {

  @LocalServerPort
  protected int testPort;

  public static final String API_BASE_PATH = "/api/v1";

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

    response.then()
      .statusCode(HttpStatus.CREATED_201)
      .body("data.attributes.displayName", Matchers.equalTo(displayName))
      .body("data.attributes.email", Matchers.equalTo(email))
      .body("data.id", Matchers.notNullValue());
  }

  private Response postAgent(String displayName, String email) {
    return given()
      .header("crnk-compact", "true")
      .contentType(JSON_API_CONTENT_TYPE)
      .body(getPostBody(displayName, email))
      .when()
      .post(API_BASE_PATH + "/agent");
  }

  private static Map<String, Object> getPostBody(String displayName, String email) {
    ImmutableMap.Builder<String, Object> objAttribMap = new ImmutableMap.Builder<>();
    objAttribMap.put("displayName", displayName);
    objAttribMap.put("email", email);
    return TestUtils.toJsonAPIMap("agent", objAttribMap.build(), null);
  }

}