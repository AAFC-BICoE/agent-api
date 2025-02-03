package ca.gc.aafc.agent.api.entities;

import ca.gc.aafc.agent.api.BaseIntegrationTest;
import ca.gc.aafc.agent.api.service.IdentifierService;
import ca.gc.aafc.agent.api.service.PersonService;
import ca.gc.aafc.agent.api.testsupport.factories.AgentIdentifierTypeFactory;
import ca.gc.aafc.agent.api.testsupport.factories.IdentifierFactory;
import ca.gc.aafc.agent.api.testsupport.factories.PersonFactory;
import ca.gc.aafc.dina.entity.IdentifierType;
import ca.gc.aafc.dina.service.IdentifierTypeService;

import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite to validate {@link Identifier} performs as a valid Hibernate Entity.
 */
public class IdentifierCrudIT extends BaseIntegrationTest {

  @Inject
  private IdentifierTypeService<AgentIdentifierType> identifierTypeService;

  @Inject
  private IdentifierService identifierService;

  @Inject
  private PersonService personService;

  @Test
  public void testCreate() {
    Identifier identifier = IdentifierFactory.newIdentifier().build();
    identifierService.create(identifier);
    assertNotNull(identifier.getId());
    assertNotNull(identifier.getCreatedOn());
  }

  @Test
  public void testUniqueIndex() {

    AgentIdentifierType identifierType = identifierTypeService.create(AgentIdentifierTypeFactory.newAgentIdentifierType().build());

    Identifier identifier = IdentifierFactory.newIdentifier()
      .agentIdentifierType(identifierType)
      .build();
    identifierService.create(identifier);

    Identifier identifier2 = IdentifierFactory.newIdentifier()
            .value(identifier.getValue())
            .build();
    // should work since they are not attached to a person
    identifierService.create(identifier2);
    identifierService.flush();

    Person person = personService.create(PersonFactory.newPerson().build());
    // Hibernate needs a mutable list
    person.setIdentifiers(new ArrayList<>(List.of(identifier, identifier2)));

    assertThrows(PersistenceException.class, ()-> personService.update(person));
  }

  @Test
  public void testFind() {
    Identifier identifier = IdentifierFactory.newIdentifier().build();
    identifierService.create(identifier);
    identifierService.flush();

    assertEquals(identifier.getId(),
            identifierService.findOne(identifier.getUuid(), Identifier.class).getId());
  }

  @Test
  public void testRemove() {
    Identifier identifier = IdentifierFactory.newIdentifier().build();
    identifierService.create(identifier);
    identifierService.flush();

    identifierService.delete(identifier);
    assertNull(identifierService.findOne(identifier.getUuid(), Identifier.class));
  }

}
