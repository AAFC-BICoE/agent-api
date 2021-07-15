package ca.gc.aafc.agent.api.service;

import ca.gc.aafc.agent.api.dto.PersonDto;
import ca.gc.aafc.agent.api.entities.Person;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.search.messaging.producer.MessageProducer;
import ca.gc.aafc.dina.service.MessageProducingService;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class PersonService extends MessageProducingService<Person> {

  public PersonService(
    @NonNull BaseDAO baseDAO,
    @NonNull SmartValidator smartValidator,
    Optional<MessageProducer> producer
  ) {
    super(baseDAO, smartValidator, producer, PersonDto.TYPENAME);
  }

  @Override
  protected void preCreate(Person entity) {
    entity.setUuid(UUID.randomUUID());
    normalizeStrings(entity);
  }

  @Override
  protected void preUpdate(Person entity) {
    normalizeStrings(entity);
  }

  private void normalizeStrings(Person entity) {
    entity.setFamilyNames(StringUtils.normalizeSpace(entity.getFamilyNames()));
    entity.setGivenNames(StringUtils.normalizeSpace(entity.getGivenNames()));
    entity.setDisplayName(StringUtils.normalizeSpace(entity.getDisplayName()));
    entity.setAliases(entity.getAliases() != null
      ? Stream.of(entity.getAliases()).map(StringUtils::normalizeSpace).toArray(String[]::new)
      : null);
  }

}
