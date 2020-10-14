package ca.gc.aafc.agent.api;

import ca.gc.aafc.agent.api.dto.OrganizationDto;
import ca.gc.aafc.agent.api.entities.Organization;
import ca.gc.aafc.agent.api.entities.OrganizationNameTranslation;
import ca.gc.aafc.dina.service.DinaService;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import com.google.common.collect.ImmutableMap;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    OrganizationDto expected = newOrgDTO();

    String id = super.sendPost("organization", mapOrg(expected))
      .extract().jsonPath().getString("data.id");

    validateResultWithId(expected, id);
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

  @Test
  void patch_nameTranslationAdded() {
    OrganizationDto dto = newOrgDTO();

    String id = super.sendPost("organization", mapOrg(dto))
      .extract().jsonPath().getString("data.id");

    dto.getNameTranslations()
      .add(OrganizationNameTranslation.builder().language("new lang").value("new Val").build());

    sendPatch("organization", id, ImmutableMap.of(
      "data", ImmutableMap.of(
        "type", "organization",
        "attributes", JsonAPITestHelper.toAttributeMap(dto)
      )));

    validateResultWithId(dto, id);
  }

  @Test
  void patch_nameTranslationRemoved() {
    OrganizationDto dto = newOrgDTO();
    dto.getNameTranslations()
      .add(OrganizationNameTranslation.builder().language("new lang").value("new Val").build());

    String id = super.sendPost("organization", mapOrg(dto))
      .extract().jsonPath().getString("data.id");

    dto.getNameTranslations().remove(0);

    sendPatch("organization", id, ImmutableMap.of(
      "data", ImmutableMap.of(
        "type", "organization",
        "attributes", JsonAPITestHelper.toAttributeMap(dto)
      )));

    validateResultWithId(dto, id);
  }

  @Test
  void patch_EmptyPatch_TranslationsRemain() {
    OrganizationDto expected = newOrgDTO();

    String id = super.sendPost("organization", mapOrg(expected))
      .extract().jsonPath().getString("data.id");

    sendPatch("organization", id, ImmutableMap.of(
      "data", ImmutableMap.of(
        "type", "organization",
        "attributes", Collections.emptyMap()
      )));

    validateResultWithId(expected, id);
  }

  private void validateResultWithId(OrganizationDto expectedDTO, String id) {
    ValidatableResponse response = sendGet("organization", id);  response.log().all(true);//TODO remove me
    response.body("data.attributes.name", Matchers.equalTo(expectedDTO.getName()));
    response.body(
      "data.attributes.nameTranslations",
      Matchers.hasSize(expectedDTO.getNameTranslations().size()));
    validateTranslations(response, expectedDTO.getNameTranslations());
  }

  private void validateTranslations(
    ValidatableResponse response, List<OrganizationNameTranslation> translations
  ) {
    for (int i = 0; i < translations.size(); i++) {
      OrganizationNameTranslation expectedTranslation = translations.get(i);
      response.body(
        "data.attributes.nameTranslations[" + i + "].language",
        Matchers.equalTo(expectedTranslation.getLanguage()));
      response.body(
        "data.attributes.nameTranslations[" + i + "].value",
        Matchers.equalTo(expectedTranslation.getValue()));
    }
  }

  private static Map<String, Object> mapOrg(OrganizationDto dto) {
    return JsonAPITestHelper.toJsonAPIMap(
      "organization", JsonAPITestHelper.toAttributeMap(dto), null, null);
  }

  private static OrganizationDto newOrgDTO() {
    OrganizationDto dto = new OrganizationDto();
    dto.setName(RandomStringUtils.randomAlphabetic(5));
    OrganizationNameTranslation translation = OrganizationNameTranslation.builder()
      .value(RandomStringUtils.randomAlphabetic(5))
      .language(RandomStringUtils.randomAlphabetic(5)).build();
    dto.setNameTranslations(new ArrayList<>(Collections.singletonList(translation)));
    return dto;
  }

}
