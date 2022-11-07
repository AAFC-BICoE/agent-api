package ca.gc.aafc.agent.api.testsupport.fixtures;

import ca.gc.aafc.agent.api.dto.PersonDto;

import java.util.List;

public class PersonTestFixture {
  public static String email = "test@canada.ca";
  public static String displayName = "test user";
  public static String givenNames = "Jane";
  public static String familyNames = "Doe";
  public static List<String> aliases = List.of("alias1", "alias2");
  public static String webpage = "https://github.com/DINA-Web";
  public static String remarks = "this is a mock remark";

  public static PersonDto newPerson() {
    PersonDto person = new PersonDto();

    person.setDisplayName(displayName);
    person.setGivenNames(givenNames);
    person.setFamilyNames(familyNames);
    person.setEmail(email);
    person.setWebpage(webpage);
    person.setRemarks(remarks);

    return person;
  }

}
