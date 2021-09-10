package ca.gc.aafc.agent.api.entities;

import ca.gc.aafc.agent.api.BaseIntegrationTest;
import ca.gc.aafc.agent.api.entities.Identifier.IdentifierType;
import ca.gc.aafc.agent.api.testsupport.factories.OrganizationFactory;
import ca.gc.aafc.agent.api.testsupport.factories.PersonFactory;
import ca.gc.aafc.dina.service.DinaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

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
  private final static Identifier IDENTIFIER = Identifier.builder()
    .type(IdentifierType.WIKIDATA)
    .uri(URI.create("https://www.wikidata.org/wiki/Q51044"))
    .build();
  private final static Identifiers IDENTIFIERS = Identifiers.builder()
    .identifiers(List.of(IDENTIFIER))
    .build();

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
    personUnderTest.setIdentifiers(IDENTIFIERS);
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
  public void testRemove() {
    personService.delete(personUnderTest);
    assertNull(getPersonUnderTest());
  }

  private Person getPersonUnderTest() {
    return personService.findOne(personUnderTest.getUuid(), Person.class);
  }

}
