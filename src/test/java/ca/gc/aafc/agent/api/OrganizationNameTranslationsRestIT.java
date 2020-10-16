package ca.gc.aafc.agent.api;

import ca.gc.aafc.agent.api.dto.OrganizationDto;
import ca.gc.aafc.agent.api.dto.OrganizationNameTranslationDto;
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
public class OrganizationNameTranslationsRestIT extends BaseRestAssuredTest {

  @Inject
  private DinaService<Organization> service;

  protected OrganizationNameTranslationsRestIT() {
    super("/api/v1/");
  }

  @Test
  void post_OrgPostedWithTranslations_TranslationsPersisted() {
    OrganizationDto expected = newOrgDTO();

    String id = super.sendPost("organization", mapOrg(expected))
      .extract().jsonPath().getString("data.id");

    validateResultWithId(expected, id);
  }

  @Test
  void delete_OrgDeleted_OrphansRemoved() {
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
  void patch_OrgUpdatedWithTranslation_TranslationAdded() {
    OrganizationDto dto = newOrgDTO();

    String id = super.sendPost("organization", mapOrg(dto))
      .extract().jsonPath().getString("data.id");

    dto.getNames()
      .add(OrganizationNameTranslationDto.builder().languageCode("ne").name("new Val").build());

    sendPatch("organization", id, ImmutableMap.of(
      "data", ImmutableMap.of(
        "type", "organization",
        "attributes", JsonAPITestHelper.toAttributeMap(dto)
      )));

    validateResultWithId(dto, id);
  }

  @Test
  void patch_TranslationRemovedFromOrg_TranslationRemoved() {
    OrganizationDto dto = newOrgDTO();
    dto.getNames()
      .add(OrganizationNameTranslationDto.builder().languageCode("ne").name("new Val").build());

    String id = super.sendPost("organization", mapOrg(dto))
      .extract().jsonPath().getString("data.id");

    dto.getNames().remove(0);

    sendPatch("organization", id, ImmutableMap.of(
      "data", ImmutableMap.of(
        "type", "organization",
        "attributes", JsonAPITestHelper.toAttributeMap(dto)
      )));

    validateResultWithId(dto, id);
  }

  @Test
  void patch_EmptyTranslationsListSubmitted_AllTranslationsRemoved() {
    OrganizationDto dto = newOrgDTO();

    String id = super.sendPost("organization", mapOrg(dto))
      .extract().jsonPath().getString("data.id");

    sendPatch("organization", id, ImmutableMap.of(
      "data", ImmutableMap.of(
        "type", "organization",
        "attributes", ImmutableMap.of("names", Collections.emptyList())
      )));

    dto.getNames().clear();
    validateResultWithId(dto, id);
  }

  @Test
  void patch_NullTranslationsListSubmitted_AllTranslationsRemoved() {
    OrganizationDto dto = newOrgDTO();

    String id = super.sendPost("organization", mapOrg(dto))
      .extract().jsonPath().getString("data.id");

    sendPatch("organization", id, ImmutableMap.of(
      "data", ImmutableMap.of(
        "type", "organization",
        "attributes", Collections.singletonMap("names", null)
      )));

    dto.getNames().clear();
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
    ValidatableResponse response = sendGet("organization", id);
    response.body(
      "data.attributes.names",
      Matchers.hasSize(expectedDTO.getNames().size()));
    validateTranslations(response, expectedDTO.getNames());
  }

  private void validateTranslations(
    ValidatableResponse response, List<OrganizationNameTranslationDto> translations
  ) {
    for (int i = 0; i < translations.size(); i++) {
      OrganizationNameTranslationDto expectedTranslation = translations.get(i);
      response.body(
        "data.attributes.names[" + i + "].languageCode",
        Matchers.equalTo(expectedTranslation.getLanguageCode()));
      response.body(
        "data.attributes.names[" + i + "].name",
        Matchers.equalTo(expectedTranslation.getName()));
    }
  }

  private static Map<String, Object> mapOrg(OrganizationDto dto) {
    return JsonAPITestHelper.toJsonAPIMap(
      "organization", JsonAPITestHelper.toAttributeMap(dto), null, null);
  }

  private static OrganizationDto newOrgDTO() {
    OrganizationDto dto = new OrganizationDto();
    OrganizationNameTranslationDto translation = OrganizationNameTranslationDto.builder()
      .name(RandomStringUtils.randomAlphabetic(5))
      .languageCode(RandomStringUtils.randomAlphabetic(2)).build();
    dto.setNames(new ArrayList<>(Collections.singletonList(translation)));
    return dto;
  }

}
