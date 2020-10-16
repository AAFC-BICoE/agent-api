package ca.gc.aafc.agent.api.openapi;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.transaction.Transactional;

import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import com.google.common.collect.ImmutableMap;

import org.apache.http.client.utils.URIBuilder;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import ca.gc.aafc.agent.api.entities.Organization;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.DatabaseSupportService;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.specs.OpenAPI3Assertions;
import io.crnk.core.engine.http.HttpStatus;
import io.restassured.response.ValidatableResponse;
import lombok.SneakyThrows;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.yml")
@ContextConfiguration(initializers = { PostgresTestContainerInitializer.class })
@Transactional
public class OrganizationOpenApiIT extends BaseRestAssuredTest {

  public static final String API_BASE_PATH = "/api/v1/organization/";
  private static final String SPEC_HOST = "raw.githubusercontent.com";
  private static final String SPEC_PATH = "DINA-Web/agent-specs/master/schema/organization.yml";
  private static final String SCHEMA_NAME = "Organization";

  private static final URIBuilder URI_BUILDER = new URIBuilder();
  static {
    URI_BUILDER.setScheme("https");
    URI_BUILDER.setHost(SPEC_HOST);
    URI_BUILDER.setPath(SPEC_PATH);
  }

  @Inject
  private DatabaseSupportService databaseSupportService;

  private static URL specUrl;

  @SneakyThrows({ MalformedURLException.class, URISyntaxException.class })
  protected OrganizationOpenApiIT() {
    super(API_BASE_PATH);
    specUrl = URI_BUILDER.build().toURL();
  }

  @Test
  public void post_NewOrganization_ReturnsOkayAndBody() {
    String name = "test-organization";
    List<String> aliases = List.of("alias1", "alias2");

    ValidatableResponse response = super.sendPost(
      "",
      JsonAPITestHelper.toJsonAPIMap(
        "organization",
        new ImmutableMap.Builder<String, Object>()
          .put("aliases", aliases)
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
