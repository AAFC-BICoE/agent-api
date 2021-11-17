package ca.gc.aafc.agent.api.openapi;

import ca.gc.aafc.agent.api.entities.Organization;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.DatabaseSupportService;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.specs.OpenAPI3Assertions;
import com.google.common.collect.ImmutableMap;
import io.restassured.response.ValidatableResponse;
import lombok.SneakyThrows;
import org.apache.http.client.utils.URIBuilder;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@TestPropertySource(properties = {
  "spring.config.additional-location=classpath:application-test.yml",
  "dev-user.enabled=true"})
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
@Transactional
public class OrganizationOpenApiIT extends BaseRestAssuredTest {

  public static final String API_BASE_PATH = "/api/v1/organization/";
  private static final String SCHEMA_NAME = "Organization";

  @Inject
  private DatabaseSupportService databaseSupportService;

  private static URL specUrl;

  @SneakyThrows({MalformedURLException.class, URISyntaxException.class})
  protected OrganizationOpenApiIT() {
    super(API_BASE_PATH);
    specUrl = createSchemaUriBuilder(OpenAPIConstants.SPEC_HOST, OpenAPIConstants.SPEC_PATH).build()
        .toURL();
  }

  @Test
  public void post_NewOrganization_ReturnsOkayAndBody() {
    List<String> aliases = List.of("alias1", "alias2");

    ValidatableResponse response = super.sendPost(
      "",
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

    response
      .body("data.attributes.aliases", Matchers.equalTo(aliases))
      .body("data.id", Matchers.notNullValue());
    OpenAPI3Assertions.assertRemoteSchema(specUrl, SCHEMA_NAME, response.extract().asString());

    // Cleanup:
    UUID uuid = response.extract().jsonPath().getUUID("data.id");
    databaseSupportService.deleteByProperty(Organization.class, "uuid", uuid);
  }

}
