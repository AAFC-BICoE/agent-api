package ca.gc.aafc.agent.api.service;

import java.util.UUID;

import ca.gc.aafc.dina.service.DefaultDinaService;
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
  }

}
