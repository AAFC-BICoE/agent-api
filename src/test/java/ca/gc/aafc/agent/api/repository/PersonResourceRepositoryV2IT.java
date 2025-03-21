package ca.gc.aafc.agent.api.repository;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.security.access.AccessDeniedException;

import ca.gc.aafc.agent.api.BaseIntegrationTest;
import ca.gc.aafc.agent.api.dto.IdentifierDto;
import ca.gc.aafc.agent.api.dto.OrganizationDto;
import ca.gc.aafc.agent.api.dto.PersonDto;
import ca.gc.aafc.agent.api.entities.Organization;
import ca.gc.aafc.agent.api.entities.OrganizationName;
import ca.gc.aafc.agent.api.entities.Person;
import ca.gc.aafc.agent.api.service.OrganizationService;
import ca.gc.aafc.agent.api.service.PersonService;
import ca.gc.aafc.agent.api.testsupport.factories.OrganizationFactory;
import ca.gc.aafc.agent.api.testsupport.factories.PersonFactory;
import ca.gc.aafc.agent.api.testsupport.fixtures.IdentifierTestFixture;
import ca.gc.aafc.dina.exception.ResourceGoneException;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocuments;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.inject.Inject;

/**
 * Test suite to validate the {@link PersonRepository} correctly handles
 * CRUD operations for the {@link Person} Entity.
 */
@SpringBootTest(properties = {"keycloak.enabled: true"})
public class PersonResourceRepositoryV2IT extends BaseIntegrationTest {
  private final static String GIVEN_NAMES = "Anata";
  private final static String FAMILY_NAMES = "Morgans";

  @Inject
  private PersonRepositoryV2 personResourceRepository;

  @Inject
  private OrganizationRepository organizationResourceRepository;

  @Inject
  private PersonService personService;

  @Inject
  private OrganizationService organizationService;

  @Inject
  private IdentifierRepository identifierRepository;

  private Person personUnderTest;

  private Organization organizationUnderTest;

  @BeforeEach
  public void setup() {
    personUnderTest = PersonFactory.newPerson().build();
    personUnderTest.setGivenNames(GIVEN_NAMES);
    personUnderTest.setFamilyNames(FAMILY_NAMES);
    organizationUnderTest = OrganizationFactory.newOrganization().build();
    personUnderTest.setOrganizations(new ArrayList<>(List.of(organizationUnderTest)));
    organizationUnderTest.setUuid(UUID.randomUUID());
    personUnderTest.setUuid(UUID.randomUUID());
    organizationService.create(organizationUnderTest);
    personService.create(personUnderTest);
  }

  @Test
  @WithMockKeycloakUser(username="user", groupRole = {"group 1:USER"})
  public void create_ValidPerson_PersonPersisted() throws ResourceNotFoundException, ResourceGoneException {
    PersonDto personDto = new PersonDto();
    personDto.setDisplayName(TestableEntityFactory.generateRandomNameLettersOnly(10));
    personDto.setEmail(TestableEntityFactory.generateRandomNameLettersOnly(5) + "@email.com");
    personDto.setGivenNames(GIVEN_NAMES);
    personDto.setFamilyNames(FAMILY_NAMES); 

    OrganizationDto organizationDto = new OrganizationDto();
    organizationDto.setNames(Collections.singletonList(
      OrganizationName.builder().languageCode("te").name("name").build()));
    OrganizationDto dto = organizationResourceRepository.create(organizationDto);

    JsonApiDocument docToCreate = JsonApiDocuments.createJsonApiDocumentWithRelToMany(
      UUID.randomUUID(), PersonDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(personDto),

      Map.of("organizations", List.of(JsonApiDocument.ResourceIdentifier.builder().id(dto.getUuid())
          .type(OrganizationDto.TYPENAME).build()))
    );

    var created = personResourceRepository.onCreate(docToCreate);
    UUID uuid = UUID.fromString(StringUtils.substringAfterLast(created.getBody().getLink(IanaLinkRelations.SELF).get().getHref(), "/"));

    Person result = personService.findOne(uuid, Person.class, Set.of("organizations"));
    assertEquals(personDto.getDisplayName(), result.getDisplayName());
    assertEquals(personDto.getGivenNames(), result.getGivenNames());
    assertEquals(personDto.getFamilyNames(), result.getFamilyNames());
    assertEquals(personDto.getEmail(), result.getEmail());
    assertEquals(uuid, result.getUuid());
    assertEquals("user", result.getCreatedBy());

    assertNotNull(result.getOrganizations());
  }

  @Test
  @WithMockKeycloakUser(username="user", groupRole = {"group 1:SUPER_USER"})
  public void save_PersistedPerson_WhenUserSuperUserRole_FieldsUpdated()
    throws ResourceNotFoundException, ResourceGoneException {

    String updatedEmail = "Updated_Email@email.com";
    String updatedName = "Updated_Name";

    PersonDto updatedPerson = personResourceRepository.getOne(
      personUnderTest.getUuid(), null).getDto();

    updatedPerson.setDisplayName(updatedName);
    updatedPerson.setEmail(updatedEmail);

    JsonApiDocument doc = JsonApiDocuments.createJsonApiDocument(
      updatedPerson.getUuid(), PersonDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(updatedPerson)
    );

    personResourceRepository.update(doc);

    Person result = personService.findOne(updatedPerson.getUuid(), Person.class);
    assertEquals(updatedName, result.getDisplayName());
    assertEquals(updatedEmail, result.getEmail());
  }

  @Test
  @WithMockKeycloakUser(username="user", groupRole = {"group 1:SUPER_USER"})
  public void save_PersistedPerson_WhenAddIdentifier_FieldsUpdated()
    throws ResourceNotFoundException, ResourceGoneException {

    IdentifierDto identifier = identifierRepository
      .create(IdentifierTestFixture.newIdentifier());

    PersonDto updatedPerson = personResourceRepository.getOne(
      personUnderTest.getUuid(), null).getDto();

   // updatedPerson.setIdentifiers(List.of(identifier));

    JsonApiDocument doc = JsonApiDocuments.createJsonApiDocumentWithRelToMany(
      updatedPerson.getUuid(), PersonDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(updatedPerson),
      Map.of("identifiers", List.of(JsonApiDocument.ResourceIdentifier.builder()
        .type(IdentifierDto.TYPENAME).id(identifier.getUuid()).build()))
    );

    personResourceRepository.update(doc);

    PersonDto result = personResourceRepository.getOne(updatedPerson.getUuid(), "include=identifiers").getDto();
    assertNotNull(result.getIdentifiers());
    assertEquals(identifier.getUuid(), result.getIdentifiers().getFirst().getUuid());
    assertEquals(identifier.getNamespace(), result.getIdentifiers().getFirst().getNamespace());
    assertEquals(identifier.getValue(), result.getIdentifiers().getFirst().getValue());
  }

  @Test
  @WithMockKeycloakUser(username="user", groupRole = {"group 1:USER"})
  public void save_PersistedPerson_WhenNotSuperUserRole_FieldsUpdate_Denied()
    throws ResourceNotFoundException, ResourceGoneException {
    String updatedEmail = "Updated_Email@email.com";
    String updatedName = "Updated_Name";

    PersonDto updatedPerson = personResourceRepository.getOne(
      personUnderTest.getUuid(), null).getDto();
    updatedPerson.setDisplayName(updatedName);
    updatedPerson.setEmail(updatedEmail);

    JsonApiDocument doc = JsonApiDocuments.createJsonApiDocument(
      updatedPerson.getUuid(), PersonDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(updatedPerson)
    );
    Assertions.assertThrows(AccessDeniedException.class,()-> personResourceRepository.update(doc));
  }

  @Test
  public void find_NoFieldsSelected_ReturnsAllFields() throws ResourceNotFoundException, ResourceGoneException {
    PersonDto result = personResourceRepository.getOne(
      personUnderTest.getUuid(), null).getDto();

    assertEquals(personUnderTest.getDisplayName(), result.getDisplayName());
    assertEquals(personUnderTest.getEmail(), result.getEmail());
    assertEquals(personUnderTest.getUuid(), result.getUuid());
  }

  @Test
  public void find_PersistedPerson_When_RelationSpec_Specified_ReturnsPersonWithRelations()
    throws ResourceNotFoundException, ResourceGoneException {

    PersonDto result = personResourceRepository.getOne(
      personUnderTest.getUuid(), "[include]=organizations").getDto();

    assertEquals(personUnderTest.getDisplayName(), result.getDisplayName());
    assertEquals(personUnderTest.getEmail(), result.getEmail());
    assertEquals(personUnderTest.getUuid(), result.getUuid());
    assertEquals(organizationUnderTest.getAliases()[0], result.getOrganizations().getFirst().getAliases()[0]);
  }

  @Test
  @WithMockKeycloakUser(username="user", groupRole = {"group 1:SUPER_USER"})
  public void remove_PersistedPerson_WhenSuperUserRole_PersonRemoved()
    throws ResourceNotFoundException, ResourceGoneException {
    PersonDto persistedPerson = personResourceRepository.getOne(
      personUnderTest.getUuid(), null).getDto();

    assertNotNull(personService.findOne(personUnderTest.getUuid(), Person.class));
    personResourceRepository.delete(persistedPerson.getUuid());
    assertNull(personService.findOne(personUnderTest.getUuid(), Person.class));
  }

  @Test
  @WithMockKeycloakUser(username="user", groupRole = {"group 1:USER"})
  public void remove_PersistedPerson_WhenNoSuperUserRole_PersonRemove_Denied()
    throws ResourceNotFoundException, ResourceGoneException {
    PersonDto persistedPerson = personResourceRepository.getOne(
      personUnderTest.getUuid(), null).getDto();

    assertNotNull(personService.findOne(personUnderTest.getUuid(), Person.class));
    Assertions.assertThrows(AccessDeniedException.class,()-> personResourceRepository.delete(persistedPerson.getUuid()));
    assertNotNull(personService.findOne(personUnderTest.getUuid(), Person.class));
  }

}
