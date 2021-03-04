package ca.gc.aafc.agent.api.service;

import java.util.UUID;
import java.util.stream.Stream;

import ca.gc.aafc.dina.service.DefaultDinaService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import ca.gc.aafc.agent.api.entities.Person;
import ca.gc.aafc.dina.jpa.BaseDAO;
import lombok.NonNull;

@Service
public class PersonService extends DefaultDinaService<Person> {

  public PersonService(@NonNull BaseDAO baseDAO) {
    super(baseDAO);
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
    entity.setAppellation(StringUtils.normalizeSpace(entity.getAppellation()));
    entity.setFamilyNames(StringUtils.normalizeSpace(entity.getFamilyNames()));
    entity.setGivenNames(StringUtils.normalizeSpace(entity.getGivenNames()));
    entity.setTitle(StringUtils.normalizeSpace(entity.getTitle()));
    entity.setDisplayName(StringUtils.normalizeSpace(entity.getDisplayName()));
    entity.setAliases(Stream.of(entity.getAliases()).map(StringUtils::normalizeSpace).toArray(String[]::new));
  }

}
