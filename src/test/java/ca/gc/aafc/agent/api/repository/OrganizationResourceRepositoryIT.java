package ca.gc.aafc.agent.api.repository;

import ca.gc.aafc.agent.api.BaseIntegrationTest;
import ca.gc.aafc.agent.api.dto.OrganizationDto;
import ca.gc.aafc.agent.api.entities.Organization;
import ca.gc.aafc.agent.api.entities.OrganizationName;
import ca.gc.aafc.agent.api.service.OrganizationService;
import ca.gc.aafc.agent.api.testsupport.factories.OrganizationFactory;
import ca.gc.aafc.dina.testsupport.DatabaseSupportService;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.PathSpec;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.resource.list.ResourceList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ValidationException;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {"keycloak.enabled: true"})
@Transactional
public class OrganizationResourceRepositoryIT extends BaseIntegrationTest {

  @Inject
  private OrganizationRepository organizationRepository;

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

  @WithMockKeycloakUser(username = "user", groupRole = {"group 1:staff"})
  @Test
  public void createOrganization_onSuccess_organizationPersisted() {
    OrganizationDto orgDto = new OrganizationDto();
    orgDto.setNames(Collections.singletonList(
      OrganizationName.builder().languageCode("te").name("name").build()));
    orgDto.setAliases(new String[]{"test alias"});

    OrganizationDto createdOrganization = organizationRepository.create(orgDto);
    assertNotNull(createdOrganization.getCreatedOn());

    Organization result = organizationService.findOne(createdOrganization.getUuid(), Organization.class);
    assertArrayEquals(orgDto.getAliases(), result.getAliases());
    assertEquals(createdOrganization.getUuid(), result.getUuid());
    assertEquals("user", result.getCreatedBy());
  }

  @Test
  @WithMockKeycloakUser(username = "user", groupRole = {"group 1:staff"})
  public void createOrganization_When_NameIsNull_throwBadRequestException() {
    OrganizationDto orgDto = new OrganizationDto();
    orgDto.setNames(Collections.emptyList());
    orgDto.setAliases(new String[]{"test alias"});
    ValidationException exception = Assertions.assertThrows(ValidationException.class, ()-> organizationRepository.create(orgDto));

    String expectedMessage = "An organization must have at least one name";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  @WithMockKeycloakUser(username = "user", groupRole = {"group 1:COLLECTION_MANAGER"})
  public void save_PersistedOrganization_When_User_Possess_CollectionManagerRole_FieldsUpdated() {
    String[] newAliases = new String[]{"new alias"};

    OrganizationDto updatedDto = organizationRepository.findOne(
      organizationUnderTest.getUuid(),
      new QuerySpec(OrganizationDto.class)
    );
    updatedDto.setAliases(newAliases);

    organizationRepository.save(updatedDto);

    Organization result = organizationService.findOne(updatedDto.getUuid(), Organization.class);
    assertArrayEquals(newAliases, result.getAliases());
  }

  @Test
  @WithMockKeycloakUser(username = "user", groupRole = {"group 1:COLLECTION_MANAGER"})
  public void save_add_OrganizationName_FieldsUpdated() {
    OrganizationDto updatedDto = organizationRepository.findOne(
      organizationUnderTest.getUuid(),
      new QuerySpec(OrganizationDto.class)
    );

    List<OrganizationName> names = updatedDto.getNames();
    names.add(OrganizationName.builder().languageCode("ne").name("new Val").build());
    updatedDto.setNames(names);

    organizationRepository.save(updatedDto);

    Organization result = organizationService.findOne(updatedDto.getUuid(), Organization.class);
    assertEquals(names, result.getNames());
  }

  @Test
  @WithMockKeycloakUser(username = "user", groupRole = {"group 1:COLLECTION_MANAGER"})
  public void save_remove_all_OrganizationName_FieldsUpdated() {
    OrganizationDto updatedDto = organizationRepository.findOne(
      organizationUnderTest.getUuid(),
      new QuerySpec(OrganizationDto.class)
    );
    updatedDto.setNames(Collections.emptyList());

    ValidationException exception = Assertions.assertThrows(ValidationException.class, ()-> organizationRepository.save(updatedDto));

    String expectedMessage = "An organization must have at least one name";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  @WithMockKeycloakUser(username = "user", groupRole = {"group 1:STAFF"})
  public void save_PersistedOrganization_When_User_Has_No_CollectionManager_Role_FieldsUpdate_Denied() {
    String[] newAliases = new String[]{"new alias"};

    OrganizationDto updatedDto = organizationRepository.findOne(
      organizationUnderTest.getUuid(),
      new QuerySpec(OrganizationDto.class)
    );
    updatedDto.setAliases(newAliases);

    Assertions.assertThrows(AccessDeniedException.class,()-> organizationRepository.save(updatedDto));
  }

  @Test
  public void find_NoFieldsSelected_ReturnsAllFields() {
    OrganizationDto result = organizationRepository.findOne(
      organizationUnderTest.getUuid(),
      new QuerySpec(OrganizationDto.class)
    );

    assertArrayEquals(organizationUnderTest.getAliases(), result.getAliases());
    assertEquals(organizationUnderTest.getUuid(), result.getUuid());
  }

  @Test
  @WithMockKeycloakUser(username = "user", groupRole = {"group 1:COLLECTION_MANAGER"})
  public void remove_PersistedOrganization_When_User_Possess_CollectioManagerRoleOrganizationRemoved() {
    OrganizationDto persistedOrg = organizationRepository.findOne(
      organizationUnderTest.getUuid(),
      new QuerySpec(OrganizationDto.class)
    );

    organizationRepository.save(persistedOrg);

    assertNotNull(organizationService.findOne(organizationUnderTest.getUuid(), Organization.class));
    organizationRepository.delete(persistedOrg.getUuid());
    assertNull(organizationService.findOne(organizationUnderTest.getUuid(), Organization.class));
  }

  @Test
  @WithMockKeycloakUser(username = "user", groupRole = {"group 1:STAFF"})
  public void remove_PersistedOrganization_When_User_Has_No_CollectionManager_Role_OrganizationRemove_Denied() {
    OrganizationDto persistedOrg = organizationRepository.findOne(
      organizationUnderTest.getUuid(),
      new QuerySpec(OrganizationDto.class)
    );

    assertNotNull(organizationService.findOne(organizationUnderTest.getUuid(), Organization.class));
    Assertions.assertThrows(AccessDeniedException.class,() -> organizationRepository.delete(persistedOrg.getUuid()));
    assertNotNull(organizationService.findOne(organizationUnderTest.getUuid(), Organization.class));
  }

  @Test
  @WithMockKeycloakUser(username = "user", groupRole = {"group 1:staff"})
  public void findOrganization_by_name() {
    OrganizationDto orgDto = new OrganizationDto();
    orgDto.setNames(Collections.singletonList(
      OrganizationName.builder().languageCode("te").name("test_name").build()));
    orgDto.setAliases(new String[]{"test alias"});

    OrganizationDto createdOrganization = organizationRepository.create(orgDto);
    assertEquals(2, organizationRepository.findAll(new QuerySpec(OrganizationDto.class)).size());

    QuerySpec querySpec = new QuerySpec(OrganizationDto.class);
    querySpec.addFilter(PathSpec.of("names", "name").filter(FilterOperator.EQ, "test_name"));

    ResourceList<OrganizationDto> results = organizationRepository.findAll(querySpec);
    assertEquals(1, results.size());
    assertEquals(createdOrganization.getUuid(), results.get(0).getUuid());

  }

}
