package ca.gc.aafc.agent.api.repository;

import ca.gc.aafc.agent.api.BaseIntegrationTest;
import ca.gc.aafc.agent.api.dto.OrganizationDto;
import ca.gc.aafc.agent.api.dto.OrganizationNameTranslationDto;
import ca.gc.aafc.agent.api.dto.PersonDto;
import ca.gc.aafc.agent.api.entities.Organization;
import ca.gc.aafc.agent.api.entities.Person;
import ca.gc.aafc.agent.api.testsupport.factories.OrganizationFactory;
import ca.gc.aafc.agent.api.testsupport.factories.PersonFactory;
import ca.gc.aafc.dina.testsupport.DatabaseSupportService;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import io.crnk.core.queryspec.IncludeRelationSpec;
import io.crnk.core.queryspec.QuerySpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test suite to validate the {@link PersonRepository} correctly handles
 * CRUD operations for the {@link Person} Entity.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {"keycloak.enabled: true"})
public class PersonResourceRepositoryIT extends BaseIntegrationTest {

  @Inject
  private PersonRepository personResourceRepository;

  @Inject
  private OrganizationRepository organizationResourceRepository;

  @Inject
  private DatabaseSupportService dbService;

  private Person personUnderTest;

  private Organization organizationUnderTest;

  @BeforeEach
  public void setup() {
    personUnderTest = PersonFactory.newPerson().build();
    organizationUnderTest = OrganizationFactory.newOrganization().build();
    personUnderTest.setOrganizations(Collections.singletonList(organizationUnderTest));
    dbService.save(organizationUnderTest);
    dbService.save(personUnderTest);
  }

  @Test
  @WithMockKeycloakUser(username="user", groupRole = {"group 1:staff"})
  public void create_ValidPerson_PersonPersisted() {
    PersonDto personDto = new PersonDto();
    personDto.setDisplayName(TestableEntityFactory.generateRandomNameLettersOnly(10));
    personDto.setEmail(TestableEntityFactory.generateRandomNameLettersOnly(5) + "@email.com");

    OrganizationDto organizationDto = new OrganizationDto();
    organizationDto.setNames(Collections.singletonList(
      OrganizationNameTranslationDto.builder().languageCode("te").name("name").build()));
    organizationDto.setUuid(UUID.randomUUID());
    OrganizationDto dto = organizationResourceRepository.create(organizationDto);

    personDto.setOrganizations(Collections.singletonList(dto));
    UUID uuid = personResourceRepository.create(personDto).getUuid();

    Person result = dbService.findUnique(Person.class, "uuid", uuid);
    assertEquals(personDto.getDisplayName(), result.getDisplayName());
    assertEquals(personDto.getEmail(), result.getEmail());
    assertEquals(uuid, result.getUuid());
    assertEquals("user", result.getCreatedBy());
    assertNotNull(result.getOrganizations());
  }

  @Test
  @WithMockKeycloakUser(username="user", groupRole = {"group 1:COLLECTION_MANAGER"})
  public void save_PersistedPerson_When_User_Possess_CollectioManagerRole_FieldsUpdated() {
    String updatedEmail = "Updated_Email@email.com";
    String updatedName = "Updated_Name";
   PersonDto updatedPerson = personResourceRepository.findOne(
      personUnderTest.getUuid(),
      new QuerySpec(PersonDto.class)
    );
    updatedPerson.setDisplayName(updatedName);
    updatedPerson.setEmail(updatedEmail);

    personResourceRepository.save(updatedPerson);

    Person result = dbService.findUnique(Person.class, "uuid", updatedPerson.getUuid());
    assertEquals(updatedName, result.getDisplayName());
    assertEquals(updatedEmail, result.getEmail());
  }

  @Test
  @WithMockKeycloakUser(username="user", groupRole = {"group 1: STAFF"})
  public void save_PersistedPerson_When_User_Has_No_CollectionManager_Role_FieldsUpdate_Denied() {
    String updatedEmail = "Updated_Email@email.com";
    String updatedName = "Updated_Name";

    PersonDto updatedPerson = personResourceRepository.findOne(
      personUnderTest.getUuid(),
      new QuerySpec(PersonDto.class)
    );
    updatedPerson.setDisplayName(updatedName);
    updatedPerson.setEmail(updatedEmail);

    Assertions.assertThrows(AccessDeniedException.class,()-> personResourceRepository.save(updatedPerson));

  }

  @Test
  public void find_NoFieldsSelected_ReturnsAllFields() {
    PersonDto result = personResourceRepository.findOne(
      personUnderTest.getUuid(),
      new QuerySpec(PersonDto.class)
    );

    assertEquals(personUnderTest.getDisplayName(), result.getDisplayName());
    assertEquals(personUnderTest.getEmail(), result.getEmail());
    assertEquals(personUnderTest.getUuid(), result.getUuid());
  }

  @Test
  public void find_PersistedPerson_When_RelationSpec_Specified_ReturnsPersonWithRelations() {

    QuerySpec querySpec = new QuerySpec(PersonDto.class);
    QuerySpec orgSpec = new QuerySpec(OrganizationDto.class);

    List<IncludeRelationSpec> includeRelationSpec = Stream.of("organizations")
        .map(Arrays::asList)
        .map(IncludeRelationSpec::new)
        .collect(Collectors.toList());

    querySpec.setIncludedRelations(includeRelationSpec);
    querySpec.setNestedSpecs(Collections.singletonList(orgSpec));
    PersonDto result = personResourceRepository.findOne(
      personUnderTest.getUuid(),
      querySpec
    );

    assertEquals(personUnderTest.getDisplayName(), result.getDisplayName());
    assertEquals(personUnderTest.getEmail(), result.getEmail());
    assertEquals(personUnderTest.getUuid(), result.getUuid());
    assertEquals(organizationUnderTest.getAliases()[0], result.getOrganizations().get(0).getAliases()[0]);
  }


  @Test
  @WithMockKeycloakUser(username="user", groupRole = {"group 1:COLLECTION_MANAGER"})
  public void remove_PersistedPerson_When_User_Possess_CollectioManagerRole_PersonRemoved() {
    PersonDto persistedPerson = personResourceRepository.findOne(
      personUnderTest.getUuid(),
      new QuerySpec(PersonDto.class)
    );

    assertNotNull(dbService.find(Person.class, personUnderTest.getId()));
    personResourceRepository.delete(persistedPerson.getUuid());
    assertNull(dbService.find(Person.class, personUnderTest.getId()));
  }

  @Test
  @WithMockKeycloakUser(username="user", groupRole = {"group 1:STAFF"})
  public void remove_PersistedPerson_When_User_Has_No_CollectionManager_Role_PersonRemove_Denied() {
    PersonDto persistedPerson = personResourceRepository.findOne(
      personUnderTest.getUuid(),
      new QuerySpec(PersonDto.class)
    );

    assertNotNull(dbService.find(Person.class, personUnderTest.getId()));
    Assertions.assertThrows(AccessDeniedException.class,()-> personResourceRepository.delete(persistedPerson.getUuid()));
    assertNotNull(dbService.find(Person.class, personUnderTest.getId()));
  }

}
