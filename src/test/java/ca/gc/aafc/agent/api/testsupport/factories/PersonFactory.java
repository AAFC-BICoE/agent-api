package ca.gc.aafc.agent.api.testsupport.factories;

import ca.gc.aafc.agent.api.entities.Person;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;

public class PersonFactory implements TestableEntityFactory<Person> {

  @Override
  public Person getEntityInstance() {
    return newPerson().build();
  }

  /**
   * Static method that can be called to return a configured builder that can be
   * further customized to return the actual entity object, call the .build()
   * method on a builder.
   * 
   * @return Pre-configured builder with all mandatory fields set
   */
  public static Person.PersonBuilder newPerson() {
    return Person
      .builder()
      .displayName(TestableEntityFactory.generateRandomNameLettersOnly(15));
  }

}

