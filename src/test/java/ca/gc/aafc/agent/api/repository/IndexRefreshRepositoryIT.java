package ca.gc.aafc.agent.api.repository;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import org.springframework.hateoas.EntityModel;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.agent.api.BaseIntegrationTest;
import ca.gc.aafc.agent.api.dto.IndexRefreshDto;
import ca.gc.aafc.agent.api.dto.PersonDto;
import ca.gc.aafc.agent.api.service.IndexRefreshService;
import ca.gc.aafc.agent.api.service.PersonService;
import ca.gc.aafc.agent.api.testsupport.factories.PersonFactory;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.messaging.producer.DocumentOperationNotificationMessageProducer;
import ca.gc.aafc.dina.security.auth.DinaAdminCUDAuthorizationService;

import ca.gc.aafc.dina.messaging.message.DocumentOperationNotification;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class IndexRefreshRepositoryIT extends BaseIntegrationTest {

  @Inject
  private PersonService personService;

  @Inject
  private BaseDAO baseDAO;

  @Inject
  private DinaAdminCUDAuthorizationService dinaAdminCUDAuthorizationService;

  @Test
  public void indexRefreshRepository_onRefreshAll_messageSent() {
    // we are not using beans to avoid the RabbitMQ part
    List<DocumentOperationNotification> messages = new ArrayList<>();
    DocumentOperationNotificationMessageProducer messageProducer = messages::add;
    IndexRefreshService service = new IndexRefreshService(messageProducer, baseDAO);
    IndexRefreshRepository repo = new IndexRefreshRepository(dinaAdminCUDAuthorizationService, service);
    personService.create(PersonFactory.newPerson().build());
    IndexRefreshDto dto = new IndexRefreshDto();
    dto.setDocType(PersonDto.TYPENAME);
    repo.handlePost(EntityModel.of(dto));

    // we may get more than 1 message if the database includes records from other tests
    assertFalse(messages.isEmpty());
  }
}
