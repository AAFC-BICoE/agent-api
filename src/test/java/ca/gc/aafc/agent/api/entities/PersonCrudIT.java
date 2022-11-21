package ca.gc.aafc.agent.api.entities;

import ca.gc.aafc.agent.api.BaseIntegrationTest;
import ca.gc.aafc.agent.api.testsupport.factories.IdentifierFactory;
import ca.gc.aafc.agent.api.testsupport.factories.OrganizationFactory;
import ca.gc.aafc.agent.api.testsupport.factories.PersonFactory;
import ca.gc.aafc.dina.service.DinaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.persistence.criteria.Predicate;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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

  @Inject
  private DinaService<Identifier> identifierService;

  private Person personUnderTest;
  private Organization organizationUnderTest;

  @BeforeEach
  public void setup() {
    personUnderTest = PersonFactory.newPerson().build();
    personUnderTest.setGivenNames(GIVEN_NAMES);
    personUnderTest.setFamilyNames(FAMILY_NAMES);
    organizationUnderTest = OrganizationFactory.newOrganization().build();
    personUnderTest.setOrganizations(new ArrayList<>(List.of(organizationUnderTest)));
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
  }

  /**
   * This test is mostly used to test MultipleBagFetchException
   */
  @Test
  public void testFindMultipleRelationships() {
    Identifier identifier = identifierService.create(IdentifierFactory.newIdentifier().build());
    personUnderTest.setIdentifiers(new ArrayList<>(List.of(identifier)));

    personService.update(personUnderTest);

    final UUID personUUID = personUnderTest.getUuid();
    List<Person> personList = personService.findAll(Person.class,
            (criteriaBuilder, root, em) -> new Predicate[]{criteriaBuilder.equal(root.get("uuid"), personUUID)},
            null, 0, 1, Set.of(), Set.of("identifiers", "organizations"));
    assertEquals(1, personList.size());
  }

  @Test
  public void testRemove() {
    personService.delete(personUnderTest);
    assertNull(getPersonUnderTest());
  }

  @Test
  public void testInvalidURLValidation_throwsConstraintValidation() {
    Person person = PersonFactory.newPerson()
      .webpage("invalidurl")
      .build();

    assertThrows(ConstraintViolationException.class, 
      () -> personService.create(person));
  }

  private Person getPersonUnderTest() {
    return personService.findOne(personUnderTest.getUuid(), Person.class);
  }

}
