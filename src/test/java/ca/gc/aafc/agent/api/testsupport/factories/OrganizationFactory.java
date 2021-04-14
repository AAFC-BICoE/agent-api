package ca.gc.aafc.agent.api.testsupport.factories;

import java.util.UUID;

import ca.gc.aafc.agent.api.entities.Organization;
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
      });
  }

}
