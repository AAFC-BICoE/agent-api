package ca.gc.aafc.agent.api;

import ca.gc.aafc.agent.api.dto.OrganizationDto;
import ca.gc.aafc.agent.api.entities.Organization;
import ca.gc.aafc.agent.api.entities.OrganizationNameTranslation;
import ca.gc.aafc.dina.service.DinaService;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

import javax.inject.Inject;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Map;

@SpringBootTest(
  classes = AgentModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Transactional
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.yml")
public class OrganizationRestIT extends BaseRestAssuredTest {

  @Inject
  private DinaService<Organization> service;

  protected OrganizationRestIT() {
    super("/api/v1/");
  }

  @Test
  void post_nameTranslationsPersisted() {
    OrganizationDto dto = newOrgDTO();

    String id = super.sendPost("organization", mapOrg(dto))
      .extract().jsonPath().getString("data.id");

    ValidatableResponse response = sendGet("organization", id);
    response.body("data.attributes.nameTranslations", Matchers.hasSize(1));
    OrganizationNameTranslation expected = dto.getNameTranslations().get(0);
    response.body(
      "data.attributes.nameTranslations[0].language",
      Matchers.equalTo(expected.getLanguage()));
    response.body(
      "data.attributes.nameTranslations[0].value",
      Matchers.equalTo(expected.getValue()));
    response.log().all(true);//TODO
  }

  @Test
  void delete_nameTranslationsRemoved() {
    OrganizationDto dto = newOrgDTO();

    String id = super.sendPost("organization", mapOrg(dto))
      .extract().jsonPath().getString("data.id");

    Assertions.assertEquals(
      1,
      service.findAll(OrganizationNameTranslation.class,
        (criteriaBuilder, organizationNameTranslationRoot) -> new Predicate[0],
        null, 0, 10).size());

    sendDelete("organization", id);

    Assertions.assertEquals(
      0,
      service.findAll(OrganizationNameTranslation.class,
        (criteriaBuilder, organizationNameTranslationRoot) -> new Predicate[0],
        null, 0, 10).size());
  }

  private Map<String, Object> mapOrg(OrganizationDto dto) {
    return JsonAPITestHelper.toJsonAPIMap(
      "organization", JsonAPITestHelper.toAttributeMap(dto), null, null);
  }

  private OrganizationDto newOrgDTO() {
    OrganizationDto dto = new OrganizationDto();
    dto.setName(RandomStringUtils.randomAlphabetic(5));
    OrganizationNameTranslation translation = OrganizationNameTranslation.builder()
      .value(RandomStringUtils.randomAlphabetic(5))
      .language(RandomStringUtils.randomAlphabetic(5)).build();
    dto.setNameTranslations(Collections.singletonList(translation));
    return dto;
  }

}
