package ca.gc.aafc.agent.api.entities;

import ca.gc.aafc.agent.api.BaseIntegrationTest;
import ca.gc.aafc.agent.api.testsupport.factories.OrganizationFactory;
import ca.gc.aafc.dina.service.DinaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class OrganizationCrudIT extends BaseIntegrationTest {

  @Inject
  private DinaService<Organization> orgService;

  private Organization organizationUnderTest;

  @BeforeEach
  public void setup() {
    organizationUnderTest = OrganizationFactory.newOrganization().build();
    orgService.create(organizationUnderTest);
  }

  @Test
  public void testCreate() {
    Organization organization = OrganizationFactory.newOrganization().build();
    assertNull(organization.getId());
    orgService.create(organization);
    assertNotNull(organization.getId());
  }

  @Test
  public void testFind() {
    Organization fetchedOrganization = findOrgUnderTest();
    assertEquals(organizationUnderTest.getId(), fetchedOrganization.getId());
    assertArrayEquals(organizationUnderTest.getAliases(), fetchedOrganization.getAliases());
    assertEquals(organizationUnderTest.getUuid(), fetchedOrganization.getUuid());
    assertNotNull(fetchedOrganization.getCreatedOn());
  }

  @Test
  public void testRemove() {
    orgService.delete(organizationUnderTest);
    assertThrows(EntityNotFoundException.class, this::findOrgUnderTest);
  }

  private Organization findOrgUnderTest() {
    return orgService.findOneReferenceByNaturalId(
      Organization.class,
      organizationUnderTest.getUuid());
  }
}
