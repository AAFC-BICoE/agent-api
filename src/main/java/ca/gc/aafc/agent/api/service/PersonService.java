package ca.gc.aafc.agent.api.service;

import java.util.UUID;
import java.util.stream.Stream;

import ca.gc.aafc.dina.service.DefaultDinaService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import ca.gc.aafc.agent.api.entities.Person;
import ca.gc.aafc.dina.jpa.BaseDAO;
import lombok.NonNull;
import org.springframework.validation.SmartValidator;

@Service
public class PersonService extends DefaultDinaService<Person> {

  public PersonService(@NonNull BaseDAO baseDAO, @NonNull SmartValidator smartValidator) {
    super(baseDAO, smartValidator);
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
