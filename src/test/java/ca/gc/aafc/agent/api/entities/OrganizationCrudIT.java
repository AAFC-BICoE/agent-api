package ca.gc.aafc.agent.api.entities;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import ca.gc.aafc.agent.api.testsupport.factories.OrganizationFactory;
import ca.gc.aafc.dina.testsupport.DatabaseSupportService;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class OrganizationCrudIT {

  @Inject
  private DatabaseSupportService dbService;

  private Organization organizationUnderTest;

  @BeforeEach
  public void setup() {
    organizationUnderTest = OrganizationFactory.newOrganization().build();
    dbService.save(organizationUnderTest);
  }

  @Test
  public void testSave() {
    Organization organization = OrganizationFactory.newOrganization().build();
    assertNull(organization.getId());
    dbService.save(organization);
    assertNotNull(organization.getId());
  }

  @Test
  public void testFind() {
    Organization fetchedOrganization = dbService.find(Organization.class, organizationUnderTest.getId());
    assertEquals(organizationUnderTest.getId(), fetchedOrganization.getId());
    assertArrayEquals(organizationUnderTest.getAliases(), fetchedOrganization.getAliases());
    assertEquals(
      organizationUnderTest.getName() + "," + String.join(",", organizationUnderTest.getAliases()),
      fetchedOrganization.getNameAndAliases()
    );
    assertEquals(organizationUnderTest.getUuid(), fetchedOrganization.getUuid());
    assertNotNull(fetchedOrganization.getCreatedOn());
  }

  @Test
  public void testRemove() {
    Integer id = organizationUnderTest.getId();
    dbService.deleteById(Organization.class, id);
    assertNull(dbService.find(Organization.class, id));
  }

}
