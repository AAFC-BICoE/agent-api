package ca.gc.aafc.agent.api.repository;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.security.access.AccessDeniedException;

import com.querydsl.core.types.Ops;

import ca.gc.aafc.agent.api.BaseIntegrationTest;
import ca.gc.aafc.agent.api.dto.OrganizationDto;
import ca.gc.aafc.agent.api.entities.Organization;
import ca.gc.aafc.agent.api.entities.OrganizationName;
import ca.gc.aafc.agent.api.service.OrganizationService;
import ca.gc.aafc.agent.api.testsupport.factories.OrganizationFactory;
import ca.gc.aafc.dina.dto.JsonApiDto;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import ca.gc.aafc.dina.filter.FilterExpression;
import ca.gc.aafc.dina.filter.QueryComponent;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocuments;
import ca.gc.aafc.dina.repository.DinaRepositoryV2;
import ca.gc.aafc.dina.testsupport.DatabaseSupportService;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.PathSpec;
import io.crnk.core.queryspec.QuerySpec;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ValidationException;

@SpringBootTest(properties = {"keycloak.enabled: true"})
@Transactional
public class OrganizationRepositoryV2IT extends BaseIntegrationTest {

  @Inject
  private OrganizationRepositoryV2 organizationRepository;

  @Inject
  private OrganizationService organizationService;

  @Inject
  private DatabaseSupportService dService;

  private Organization organizationUnderTest;

  @BeforeEach
  public void setup() {
    organizationUnderTest = OrganizationFactory.newOrganization().build();
    organizationUnderTest.setUuid(UUID.randomUUID());
    organizationUnderTest.setNames(Collections.singletonList(
      OrganizationName.builder().languageCode("le").name("name").build()));
    dService.save(organizationUnderTest);
  }

  @WithMockKeycloakUser(username = "user", groupRole = {"group 1:USER"})
  @Test
  public void createOrganization_onSuccess_organizationPersisted() throws ResourceNotFoundException {
    OrganizationDto orgDto = new OrganizationDto();
    orgDto.setNames(Collections.singletonList(
      OrganizationName.builder().languageCode("te").name("name").build()));
    orgDto.setAliases(new String[]{"test alias"});

    JsonApiDocument docToCreate = JsonApiDocuments.createJsonApiDocument(
      UUID.randomUUID(), OrganizationDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(orgDto));

    var created = organizationRepository.handleCreate(docToCreate);
    UUID uuid = UUID.fromString(
      StringUtils.substringAfterLast(created.getBody().getLink(IanaLinkRelations.SELF).get().getHref(), "/"));

    Organization result = organizationService.findOne(uuid, Organization.class);
    assertArrayEquals(orgDto.getAliases(), result.getAliases());
    assertEquals(uuid, result.getUuid());
    assertEquals("user", result.getCreatedBy());
  }

  @Test
  @WithMockKeycloakUser(username = "user", groupRole = {"group 1:USER"})
  public void createOrganization_When_NameIsNull_throwBadRequestException() {
    OrganizationDto orgDto = new OrganizationDto();
    orgDto.setNames(Collections.emptyList());
    orgDto.setAliases(new String[]{"test alias"});

    JsonApiDocument docToCreate = JsonApiDocuments.createJsonApiDocument(
      UUID.randomUUID(), OrganizationDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(orgDto));

    ValidationException exception = Assertions.assertThrows(ValidationException.class, ()-> organizationRepository.handleCreate(docToCreate));

    String expectedMessage = "An organization must have at least one name";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  @WithMockKeycloakUser(username = "user", groupRole = {"group 1:SUPER_USER"})
  public void save_PersistedOrganization_WhenSuperUserRole_FieldsUpdated()
    throws ResourceNotFoundException {

    String[] newAliases = new String[]{"new alias"};

    OrganizationDto updatedDto = organizationRepository.getOne(
      organizationUnderTest.getUuid(), null).getDto();

    updatedDto.setAliases(newAliases);

    JsonApiDocument doc = JsonApiDocuments.createJsonApiDocument(
      updatedDto.getUuid(), OrganizationDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(updatedDto)
    );

    organizationRepository.update(doc);

    Organization result = organizationService.findOne(updatedDto.getUuid(), Organization.class);
    assertArrayEquals(newAliases, result.getAliases());
  }

  @Test
  @WithMockKeycloakUser(username = "user", groupRole = {"group 1:SUPER_USER"})
  public void save_add_OrganizationName_FieldsUpdated() throws ResourceNotFoundException {

    OrganizationDto updatedDto = organizationRepository.getOne(organizationUnderTest.getUuid(),
      null).getDto();

    List<OrganizationName> names = updatedDto.getNames();
    names.add(OrganizationName.builder().languageCode("ne").name("new Val").build());
    updatedDto.setNames(names);

    JsonApiDocument doc = JsonApiDocuments.createJsonApiDocument(
      updatedDto.getUuid(), OrganizationDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(updatedDto)
    );
    organizationRepository.update(doc);

    Organization result = organizationService.findOne(updatedDto.getUuid(), Organization.class);
    assertEquals(names.size(), result.getNames().size());
  }

  @Test
  @WithMockKeycloakUser(username = "user", groupRole = {"group 1:SUPER_USER"})
  public void save_remove_all_OrganizationName_FieldsUpdated() throws ResourceNotFoundException {
    OrganizationDto updatedDto = organizationRepository.getOne(organizationUnderTest.getUuid(),
      null).getDto();
    updatedDto.setNames(Collections.emptyList());

    JsonApiDocument doc = JsonApiDocuments.createJsonApiDocument(
      updatedDto.getUuid(), OrganizationDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(updatedDto)
    );

    ValidationException exception = Assertions.assertThrows(ValidationException.class, ()-> organizationRepository.update(doc));

    String expectedMessage = "An organization must have at least one name";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  @WithMockKeycloakUser(username = "user", groupRole = {"group 1:USER"})
  public void save_PersistedOrganization_WhenNoSuperUserRole_FieldsUpdate_Denied()
    throws ResourceNotFoundException {
    String[] newAliases = new String[]{"new alias"};

    OrganizationDto updatedDto = organizationRepository.getOne(organizationUnderTest.getUuid(),
      null).getDto();
    updatedDto.setAliases(newAliases);

    JsonApiDocument doc = JsonApiDocuments.createJsonApiDocument(
      updatedDto.getUuid(), OrganizationDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(updatedDto)
    );

    Assertions.assertThrows(AccessDeniedException.class,()-> organizationRepository.update(doc));
  }


  @Test
  public void find_NoFieldsSelected_ReturnsAllFields() throws ResourceNotFoundException {
    OrganizationDto result = organizationRepository.getOne(organizationUnderTest.getUuid(),
      null).getDto();

    assertArrayEquals(organizationUnderTest.getAliases(), result.getAliases());
    assertEquals(organizationUnderTest.getUuid(), result.getUuid());
  }

  @Test
  @WithMockKeycloakUser(username = "user", groupRole = {"group 1:SUPER_USER"})
  public void remove_PersistedOrganization_WhenSuperUserRole_OrganizationRemoved()
    throws ResourceNotFoundException {

    OrganizationDto persistedOrg = organizationRepository.getOne(organizationUnderTest.getUuid(),
      null).getDto();

    assertNotNull(organizationService.findOne(organizationUnderTest.getUuid(), Organization.class));
    organizationRepository.delete(persistedOrg.getUuid());
    assertNull(organizationService.findOne(organizationUnderTest.getUuid(), Organization.class));
  }

  @Test
  @WithMockKeycloakUser(username = "user", groupRole = {"group 1:USER"})
  public void remove_PersistedOrganization_WhenNoSuperUserRole_OrganizationRemove_Denied()
    throws ResourceNotFoundException {

    OrganizationDto persistedOrg = organizationRepository.getOne(organizationUnderTest.getUuid(),
      null).getDto();

    assertNotNull(organizationService.findOne(organizationUnderTest.getUuid(), Organization.class));
    Assertions.assertThrows(AccessDeniedException.class,() -> organizationRepository.delete(persistedOrg.getUuid()));
    assertNotNull(organizationService.findOne(organizationUnderTest.getUuid(), Organization.class));
  }

  @Test
  @WithMockKeycloakUser(username = "user", groupRole = {"group 1:USER"})
  public void findOrganization_by_name() throws ResourceNotFoundException {

    OrganizationDto orgDto = new OrganizationDto();
    orgDto.setNames(Collections.singletonList(
      OrganizationName.builder().languageCode("te").name("test_name").build()));
    orgDto.setAliases(new String[] {"test alias"});

    int totalCreatedOrganization = organizationRepository.getAll(QueryComponent.EMPTY).totalCount();

    JsonApiDocument docToCreate = JsonApiDocuments.createJsonApiDocument(
      UUID.randomUUID(), OrganizationDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(orgDto));

    var created = organizationRepository.handleCreate(docToCreate);
    UUID uuid = UUID.fromString(
      StringUtils.substringAfterLast(
        created.getBody().getLink(IanaLinkRelations.SELF).get().getHref(), "/"));


    assertEquals(totalCreatedOrganization + 1,
      organizationRepository.getAll(QueryComponent.EMPTY).totalCount());

    QuerySpec querySpec = new QuerySpec(OrganizationDto.class);
    querySpec.addFilter(PathSpec.of("names", "name").filter(FilterOperator.EQ, "test_name"));

    QueryComponent qc = QueryComponent.builder()
      .filters(new FilterExpression("names.name", Ops.EQ, "test_name")).build();

    DinaRepositoryV2.PagedResource<JsonApiDto<OrganizationDto>> results =
      organizationRepository.getAll(qc);
    assertEquals(1, results.totalCount());

    assertEquals(uuid, results.resourceList().getFirst().getDto().getUuid());

  }

}
