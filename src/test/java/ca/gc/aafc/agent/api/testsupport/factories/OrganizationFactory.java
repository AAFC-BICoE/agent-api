package ca.gc.aafc.agent.api.testsupport.factories;

import java.util.Collections;

import ca.gc.aafc.agent.api.entities.Organization;
import ca.gc.aafc.agent.api.entities.OrganizationName;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;

public class OrganizationFactory implements TestableEntityFactory<Organization> {

  @Override
  public Organization getEntityInstance() {
    return newOrganization().build();
  }

  public static Organization.OrganizationBuilder newOrganization() {
    return Organization.builder()
      .aliases(new String[] {
        TestableEntityFactory.generateRandomNameLettersOnly(15),
        TestableEntityFactory.generateRandomNameLettersOnly(15)
      })
      .names(Collections.singletonList(
        OrganizationName.builder()
          .languageCode(TestableEntityFactory.generateRandomNameLettersOnly(2))
          .name(TestableEntityFactory.generateRandomNameLettersOnly(15)).build()));
  }

}
