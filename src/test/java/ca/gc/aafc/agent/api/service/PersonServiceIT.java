package ca.gc.aafc.agent.api.service;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.agent.api.BaseIntegrationTest;
import ca.gc.aafc.agent.api.entities.Person;

import jakarta.transaction.Transactional;
import java.util.UUID;

@Transactional
public class PersonServiceIT extends BaseIntegrationTest {

  @Inject
  private PersonService personService;

  @Test
  public void createPerson_whenStringWhitespaceIsNotNormalized_stringWhitespaceIsNormalized() {
    UUID userProvidedUUID = UUID.randomUUID();
    Person person = Person.builder()
        .uuid(userProvidedUUID)
        .familyNames(" Family\nNames ")
        .givenNames("  Given                 Names  ")
        .displayName(" Display  Name  ")
        .aliases(new String[] { "  Alias 1", "Alias    2", "Alias\n3" })
        .build();

    personService.create(person);

    assertEquals("Family Names", person.getFamilyNames());
    assertEquals("Given Names", person.getGivenNames());
    assertEquals("Display Name", person.getDisplayName());
    assertEquals(userProvidedUUID, person.getUuid());
    assertArrayEquals(new String[] { "Alias 1", "Alias 2", "Alias 3" }, person.getAliases());

    // make sure duplicate detection is working
    Person person2 = Person.builder()
        .familyNames("Family Names")
        .givenNames("Given Names")
        .displayName("Display Name")
        .build();
    assertThrows(IllegalStateException.class, () -> personService.create(person2));

    // make sure we can accept the duplicate
    Person person3 = Person.builder()
        .familyNames("Family Names")
        .givenNames("Given Names")
        .displayName("Display Name")
        .allowDuplicateName(true)
        .build();
    personService.create(person3);
  }
  
}
