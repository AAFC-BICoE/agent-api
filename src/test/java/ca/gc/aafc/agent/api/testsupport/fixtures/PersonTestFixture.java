package ca.gc.aafc.agent.api.testsupport.fixtures;

import ca.gc.aafc.agent.api.dto.PersonDto;

import java.util.List;

public class PersonTestFixture {
  public static String EMAIL = "test@canada.ca";
  public static String DISPLAYNAME = "test user";
  public static String GIVENNAMES = "Jane";
  public static String FAMILYNAMES = "Doe";
  public static List<String> ALIASES = List.of("alias1", "alias2");
  public static String WEBPAGE = "https://github.com/DINA-Web";
  public static String REMARKS = "this is a mock remark";

  public static PersonDto newPerson() {
    PersonDto person = new PersonDto();

    person.setDisplayName(DISPLAYNAME);
    person.setGivenNames(GIVENNAMES);
    person.setFamilyNames(FAMILYNAMES);
    person.setEmail(EMAIL);
    person.setWebpage(WEBPAGE);
    person.setRemarks(REMARKS);

    return person;
  }

}
