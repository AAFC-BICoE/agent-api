package ca.gc.aafc.agent.api.entities;

import ca.gc.aafc.agent.api.testsupport.factories.PersonOrganizationFactory;
import ca.gc.aafc.dina.testsupport.DatabaseSupportService;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

public class PersonOrganizationCRUDIT {

  @Inject
  private DatabaseSupportService dbService;  

  private PersonOrganization personOrganizationUnderTest;

  @BeforeEach
  void setUp() {
    personOrganizationUnderTest = PersonOrganizationFactory.newPersonOrganization().build();
    dbService.save(personOrganizationUnderTest.getOrganization());
    dbService.save(personOrganizationUnderTest.getPerson());
    dbService.save(personOrganizationUnderTest);
  }

  public void testSave() {
    Assertions.assertNotNull(personOrganizationUnderTest.getId());
  }

  public void testFind() {
    PersonOrganization fetchedPersonOrganization = dbService.find(
      PersonOrganization.class,
      this.personOrganizationUnderTest.getId());

    Assertions.assertNotNull(fetchedPersonOrganization.getPerson());
    Assertions.assertNotNull(personOrganizationUnderTest.getOrganization());
  }

  public void testRemove() {
    dbService.deleteById(PersonOrganization.class, personOrganizationUnderTest.getId());
    Assertions.assertNull(dbService.find(PersonOrganization.class, personOrganizationUnderTest.getId()));
  }

}
