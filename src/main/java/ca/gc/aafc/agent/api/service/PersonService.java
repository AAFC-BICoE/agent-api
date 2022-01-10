package ca.gc.aafc.agent.api.service;

import java.util.UUID;
import java.util.stream.Stream;

import ca.gc.aafc.agent.api.dto.PersonDto;

import ca.gc.aafc.dina.service.MessageProducingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import ca.gc.aafc.agent.api.entities.Person;
import ca.gc.aafc.dina.jpa.BaseDAO;
import lombok.NonNull;
import org.springframework.validation.SmartValidator;

@Service
public class PersonService extends MessageProducingService<Person> {

  public PersonService(@NonNull BaseDAO baseDAO, @NonNull SmartValidator smartValidator, ApplicationEventPublisher eventPublisher) {
    super(baseDAO, smartValidator, PersonDto.TYPENAME, eventPublisher);
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
    entity.setAliases(entity.getAliases() != null ?
        Stream.of(entity.getAliases()).map(StringUtils::normalizeSpace).toArray(String[]::new) :
        null);
  }

}
