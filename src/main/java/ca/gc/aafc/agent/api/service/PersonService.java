package ca.gc.aafc.agent.api.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import ca.gc.aafc.agent.api.entities.Person;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DinaService;
import lombok.NonNull;

@Service
public class PersonService extends DinaService<Person> {

  public PersonService(@NonNull BaseDAO baseDAO) {
    super(baseDAO);
  }

  @Override
  protected Person preCreate(Person entity) {
    entity.setUuid(UUID.randomUUID());
    return entity;
  }

  @Override
  protected void preDelete(Person entity) {

  }

  @Override
  protected Person preUpdate(Person entity) {
    return entity;
  }

}
