package ca.gc.aafc.agent.api;

import ca.gc.aafc.agent.api.dto.OrganizationDto;
import ca.gc.aafc.agent.api.entities.OrganizationNameTranslation;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import javax.transaction.Transactional;
import java.util.Collections;

@SpringBootTest(
  classes = AgentModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Transactional
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.yml")
public class OrganizationRestIT extends BaseRestAssuredTest {

  protected OrganizationRestIT() {
    super("/api/v1/");
  }

  @Test
  void post_customFieldsResolved() {
    OrganizationDto dto = new OrganizationDto();
    dto.setName("d");
    dto.setNameTranslations(Collections.singletonList(OrganizationNameTranslation.builder()
      .value("testValue").language("testlang").build()));

    String id = super.sendPost(
      "organization",
      JsonAPITestHelper.toJsonAPIMap(
        "organization", JsonAPITestHelper.toAttributeMap(dto), null, null))
      .extract()
      .jsonPath()
      .getString("data.id");

    sendGet("organization", id).log().all(true);
  }

}
