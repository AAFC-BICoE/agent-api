package ca.gc.aafc.agent.api.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import ca.gc.aafc.agent.api.entities.Agent;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DinaService;
import lombok.NonNull;

@Service
public class AgentService extends DinaService<Agent> {

  public AgentService(@NonNull BaseDAO baseDAO) {
    super(baseDAO);
  }

  @Override
  protected Agent preCreate(Agent entity) {
    entity.setUuid(UUID.randomUUID());
    return entity;
  }

  @Override
  protected void preDelete(Agent entity) {

  }

  @Override
  protected Agent preUpdate(Agent entity) {
    return entity;
  }

}
