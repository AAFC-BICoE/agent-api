package ca.gc.aafc.agent.api.rest;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import ca.gc.aafc.agent.api.AgentModuleApiLauncher;
import ca.gc.aafc.agent.api.BaseIntegrationTest;
import ca.gc.aafc.agent.api.dto.IdentifierDto;
import ca.gc.aafc.agent.api.dto.PersonDto;
import ca.gc.aafc.agent.api.entities.Identifier;
import ca.gc.aafc.agent.api.entities.Person;
import ca.gc.aafc.agent.api.testsupport.fixtures.IdentifierTestFixture;
import ca.gc.aafc.dina.jsonapi.JsonApiBulkResourceIdentifierDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocuments;
import ca.gc.aafc.dina.repository.DinaRepositoryV2;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.DatabaseSupportService;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import java.util.UUID;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * Test suite to validate correct HTTP and JSON API responses for {@link Identifier}
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
@Import(BaseIntegrationTest.CollectionModuleTestConfiguration.class)
@Transactional
public class IdentifierRestJsonIT extends BaseRestAssuredTest {

  @Inject
  private DatabaseSupportService databaseSupportService;

  public static final String API_BASE_PATH = "/api/v1/identifier/";

  protected IdentifierRestJsonIT() {
    super(API_BASE_PATH);
  }

  @Test
  public void loadBatch_PersistedPerson_ReturnsOkayAndBody() {
    JsonApiDocument identifierDoc = JsonApiDocuments.createJsonApiDocument(
      null, IdentifierDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(IdentifierTestFixture.newIdentifier())
    );

    String id = sendPost("", identifierDoc).extract().jsonPath().get("data.id");

    // to update when base-api 0.143 is available
    var request = RestAssured.given().config(RestAssured.config().encoderConfig(
        EncoderConfig.encoderConfig().defaultCharsetForContentType("UTF-8",
          DinaRepositoryV2.JSON_API_BULK)))
      .contentType(DinaRepositoryV2.JSON_API_BULK).port(this.testPort).basePath(this.basePath);

    var bulkLoadDocument = JsonApiBulkResourceIdentifierDocument.builder();
    bulkLoadDocument.addData(JsonApiDocument.ResourceIdentifier.builder()
      .type(PersonDto.TYPENAME)
      .id(UUID.fromString(id))
      .build());

    // use that function with dina-base 0.143
    var postRequest = request.body(bulkLoadDocument.build()).post(DinaRepositoryV2.JSON_API_BULK_LOAD_PATH);

    var response = postRequest.then().log().ifValidationFails().statusCode(200);
    response.body("data[0].id", Matchers.equalTo(id));

    // Cleanup:
    databaseSupportService.deleteByProperty(Person.class, "uuid", UUID.fromString(id));
  }

}
