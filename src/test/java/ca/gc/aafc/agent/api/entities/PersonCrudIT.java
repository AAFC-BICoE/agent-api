package ca.gc.aafc.agent.api.entities;

import ca.gc.aafc.agent.api.BaseIntegrationTest;
import ca.gc.aafc.agent.api.entities.Identifier.IdentifierType;
import ca.gc.aafc.agent.api.testsupport.factories.OrganizationFactory;
import ca.gc.aafc.agent.api.testsupport.factories.PersonFactory;
import ca.gc.aafc.dina.service.DinaService;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.persistence.PersistenceException;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite to validate {@link Person} performs as a valid Hibernate Entity.
 */
public class PersonCrudIT extends BaseIntegrationTest {

  private final static String GIVEN_NAMES = "Anata";
  private final static String FAMILY_NAMES = "Morgans";

  @Inject
  private DinaService<Person> personService;

  @Inject
  private DinaService<Organization> orgService;

  private Person personUnderTest;
  private Organization organizationUnderTest;

  @BeforeEach
  public void setup() {
    personUnderTest = PersonFactory.newPerson().build();
    personUnderTest.setGivenNames(GIVEN_NAMES);
    personUnderTest.setFamilyNames(FAMILY_NAMES);
    personUnderTest.setIdentifiers(Collections.singletonList(getUniqueIdentifier(IdentifierType.ORCID)));
    organizationUnderTest = OrganizationFactory.newOrganization().build();
    personUnderTest.setOrganizations(Collections.singletonList(organizationUnderTest));
    orgService.create(organizationUnderTest);
    personService.create(personUnderTest);
  }

  @Test
  public void testCreate() {
    Person person = PersonFactory.newPerson().build();
    organizationUnderTest = OrganizationFactory.newOrganization().build();
    person.setOrganizations(Collections.singletonList(organizationUnderTest));
    assertNull(person.getId());
    assertNull(organizationUnderTest.getId());
    orgService.create(organizationUnderTest);
    personService.create(person);
    assertNotNull(person.getId());
    assertNotNull(organizationUnderTest.getId());
  }

  @Test
  public void testFind() {
    Person fetchedPerson = getPersonUnderTest();
    assertEquals(personUnderTest.getId(), fetchedPerson.getId());
    assertEquals(personUnderTest.getDisplayName(), fetchedPerson.getDisplayName());
    assertEquals(personUnderTest.getGivenNames(), fetchedPerson.getGivenNames());
    assertEquals(personUnderTest.getFamilyNames(), fetchedPerson.getFamilyNames());
    assertEquals(personUnderTest.getEmail(), fetchedPerson.getEmail());
    assertEquals(personUnderTest.getUuid(), fetchedPerson.getUuid());
    assertNotNull(fetchedPerson.getCreatedOn());
    assertNotNull(fetchedPerson.getOrganizations());
    assertEquals(
      organizationUnderTest.getId(),
      fetchedPerson.getOrganizations().iterator().next().getId());
    assertEquals(personUnderTest.getIdentifiers(), fetchedPerson.getIdentifiers());
  }

  @Test
  public void whenNoIdentifiers_OperationAllowed() {
    Person person1 = PersonFactory.newPerson().build();
    person1.setIdentifiers(null);
    Person person2 = PersonFactory.newPerson().build();
    person2.setIdentifiers(null);
    Assertions.assertDoesNotThrow(() -> personService.create(person1));
    Assertions.assertDoesNotThrow(() -> personService.create(person2));
  }

  @Test
  public void wikiDataUniqueIndex() {
    Identifier wikiId = getUniqueIdentifier(IdentifierType.WIKIDATA);

    Person wikiUser = PersonFactory.newPerson().build();
    wikiUser.setIdentifiers(Collections.singletonList(wikiId));
    Person wikiUser2 = PersonFactory.newPerson().build();
    wikiUser2.setIdentifiers(Collections.singletonList(wikiId));
    personService.create(wikiUser);
    Assertions.assertThrows(PersistenceException.class, () -> personService.create(wikiUser2));
  }

  @Test
  public void orcidUniqueIndex() {
    Identifier orcid = getUniqueIdentifier(IdentifierType.ORCID);

    Person orcidUser = PersonFactory.newPerson().build();
    orcidUser.setIdentifiers(Collections.singletonList(orcid));
    Person orcidUser2 = PersonFactory.newPerson().build();
    orcidUser2.setIdentifiers(Collections.singletonList(orcid));
    personService.create(orcidUser);
    Assertions.assertThrows(PersistenceException.class, () -> personService.create(orcidUser2));
  }

  @Test
  public void testRemove() {
    personService.delete(personUnderTest);
    assertNull(getPersonUnderTest());
  }

  private Person getPersonUnderTest() {
    return personService.findOne(personUnderTest.getUuid(), Person.class);
  }

  private static Identifier getUniqueIdentifier(IdentifierType type) {
    return Identifier.builder()
        .type(type)
        .uri(URI.create("https://www.ORCID.org/ORCID/" +
            TestableEntityFactory.generateRandomName(5)))
        .build();
  }

}
