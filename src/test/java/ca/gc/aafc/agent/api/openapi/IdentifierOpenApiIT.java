package ca.gc.aafc.agent.api.openapi;

import ca.gc.aafc.agent.api.dto.IdentifierDto;
import ca.gc.aafc.agent.api.testsupport.fixtures.IdentifierTestFixture;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.specs.OpenAPI3Assertions;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {
        "spring.config.additional-location=classpath:application-test.yml",
        "dev-user.enabled=true"})
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
public class IdentifierOpenApiIT extends BaseRestAssuredTest {

  protected IdentifierOpenApiIT() {
    super("/api/v1/");
  }

  @SneakyThrows
  @Test
  void identifier_SpecValid() {
    IdentifierDto identifierDto = IdentifierTestFixture.newIdentifier();

    OpenAPI3Assertions.assertRemoteSchema(OpenAPIConstants.AGENT_API_SPECS_URL, "Identifier",
            sendPost(IdentifierDto.TYPENAME, JsonAPITestHelper
                    .toJsonAPIMap(IdentifierDto.TYPENAME, JsonAPITestHelper.toAttributeMap(identifierDto))).extract()
                    .asString());
  }
}
