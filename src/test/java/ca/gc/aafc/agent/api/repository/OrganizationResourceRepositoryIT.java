package ca.gc.aafc.agent.api.repository;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import ca.gc.aafc.agent.api.dto.OrganizationDto;
import ca.gc.aafc.agent.api.entities.Organization;
import ca.gc.aafc.agent.api.testsupport.factories.OrganizationFactory;
import ca.gc.aafc.dina.testsupport.DatabaseSupportService;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import io.crnk.core.queryspec.QuerySpec;

@ExtendWith(SpringExtension.class)
@SpringBootTest( 
    properties = "keycloak.enabled: true" 
)
@Transactional
public class OrganizationResourceRepositoryIT {
  
  @Inject
  private OrganizationRepository organizationRepository;

  @Inject
  private DatabaseSupportService dbService;

  private Organization organizationUnderTest;

  @BeforeEach
  public void setup() {
    organizationUnderTest = OrganizationFactory.newOrganization().build();
    dbService.save(organizationUnderTest);
  }

  @WithMockKeycloakUser(username="user", groupRole = {"group 1:staff"})
  @Test
  public void createOrganization_onSuccess_organizationPersisted() {
    OrganizationDto orgDto = new OrganizationDto();
    orgDto.setName("test org");
    orgDto.setAliases(new String[] {"test alias"});

    UUID uuid = organizationRepository.create(orgDto).getUuid();

    Organization result = dbService.findUnique(Organization.class, "uuid", uuid);
    assertEquals(orgDto.getName(), result.getName());
    assertArrayEquals(orgDto.getAliases(), result.getAliases());
    assertEquals(uuid, result.getUuid());
    assertEquals("user", result.getCreatedBy());
  }

  @Test
  @WithMockKeycloakUser(username="user", groupRole = {"group 1:COLLECTION_MANAGER"})
  public void save_PersistedOrganization_When_User_Possess_CollectionManagerRole_FieldsUpdated() {
    String newName = "new name";
    String[] newAliases = new String[] {"new alias"};

    OrganizationDto updatedDto = organizationRepository.findOne(
      organizationUnderTest.getUuid(),
      new QuerySpec(OrganizationDto.class)
    );
    updatedDto.setName(newName);
    updatedDto.setAliases(newAliases);

    organizationRepository.save(updatedDto);

    Organization result = dbService.findUnique(Organization.class, "uuid", updatedDto.getUuid());
    assertEquals(newName, result.getName());
    assertArrayEquals(newAliases, result.getAliases());
  }

  @Test
  @WithMockKeycloakUser(username="user", groupRole = {"group 1:STAFF"})
  public void save_PersistedOrganization_When_User_Has_No_CollectionManager_Role_FieldsUpdate_Denied() {
    String newName = "new name";
    String[] newAliases = new String[] {"new alias"};

    OrganizationDto updatedDto = organizationRepository.findOne(
      organizationUnderTest.getUuid(),
      new QuerySpec(OrganizationDto.class)
    );
    updatedDto.setName(newName);
    updatedDto.setAliases(newAliases);

    Assertions.assertThrows(AccessDeniedException.class,()-> organizationRepository.save(updatedDto));
  }

  @Test
  public void find_NoFieldsSelected_ReturnsAllFields() {
    OrganizationDto result = organizationRepository.findOne(
      organizationUnderTest.getUuid(),
      new QuerySpec(OrganizationDto.class)
    );

    assertEquals(organizationUnderTest.getName(), result.getName());
    assertArrayEquals(organizationUnderTest.getAliases(), result.getAliases());
    assertEquals(organizationUnderTest.getUuid(), result.getUuid());
  }

  @Test
  @WithMockKeycloakUser(username="user", groupRole = {"group 1:COLLECTION_MANAGER"})
  public void remove_PersistedOrganization_When_User_Possess_CollectioManagerRoleOrganizationRemoved() {
    OrganizationDto persistedOrg = organizationRepository.findOne(
      organizationUnderTest.getUuid(),
      new QuerySpec(OrganizationDto.class)
    );

    assertNotNull(dbService.find(Organization.class, organizationUnderTest.getId()));
    organizationRepository.delete(persistedOrg.getUuid());
    assertNull(dbService.find(Organization.class, organizationUnderTest.getId()));
  }

  @Test
  @WithMockKeycloakUser(username="user", groupRole = {"group 1:STAFF"})
  public void remove_PersistedOrganization_When_User_Has_No_CollectionManager_Role_OrganizationRemove_Denied() {
    OrganizationDto persistedOrg = organizationRepository.findOne(
      organizationUnderTest.getUuid(),
      new QuerySpec(OrganizationDto.class)
    );

    assertNotNull(dbService.find(Organization.class, organizationUnderTest.getId()));
    Assertions.assertThrows(AccessDeniedException.class,() -> organizationRepository.delete(persistedOrg.getUuid()));
    assertNull(dbService.find(Organization.class, organizationUnderTest.getId()));
  }  

}
