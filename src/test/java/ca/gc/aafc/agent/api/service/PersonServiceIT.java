package ca.gc.aafc.agent.api.service;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.agent.api.BaseIntegrationTest;
import ca.gc.aafc.agent.api.entities.Person;

public class PersonServiceIT extends BaseIntegrationTest {

  @Inject
  private PersonService personService;

  @Test
  public void createPerson_whenStringWhitespaceIsNotNormalized_stringWhitespaceIsNormalized() {
    Person person = Person.builder()
        .appellation(" Appellation ")
        .familyNames(" Family\nNames ")
        .givenNames("  Given                 Names  ")
        .title("    title")
        .displayName(" Display  Name  ")
        .build();
    
    personService.create(person);

    assertEquals("Appellation", person.getAppellation());
    assertEquals("Family Names", person.getFamilyNames());
    assertEquals("Given Names", person.getGivenNames());
    assertEquals("title", person.getTitle());
    assertEquals("Display Name", person.getDisplayName());
  }
  
}