package ca.gc.aafc.agent.api.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.agent.api.BaseIntegrationTest;
import ca.gc.aafc.agent.api.entities.Person;

import java.util.UUID;

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
        .aliases(new String[] {"  Alias 1", "Alias    2", "Alias\n3"})
        .build();
    
    personService.create(person);

    assertEquals("Family Names", person.getFamilyNames());
    assertEquals("Given Names", person.getGivenNames());
    assertEquals("Display Name", person.getDisplayName());
    assertEquals(userProvidedUUID, person.getUuid());
    assertArrayEquals(new String[] {"Alias 1", "Alias 2", "Alias 3"}, person.getAliases());
  }
  
}
