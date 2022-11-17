package ca.gc.aafc.agent.api.testsupport.fixtures;

import ca.gc.aafc.agent.api.dto.OrganizationDto;
import ca.gc.aafc.agent.api.entities.OrganizationName;

import java.util.Collections;
import java.util.List;

public class OrganisationTestFixture {
  static List<OrganizationName> NAMES = Collections.singletonList(
    OrganizationName.builder().languageCode("te").name("test").build());
  public static String[] ALIASES = new String[]{"alias1", "alias2"};

  public static OrganizationDto newOrganization() {
    OrganizationDto organization = new OrganizationDto();
    organization.setNames(NAMES);
    organization.setAliases(ALIASES);

    return organization;
  }

}
