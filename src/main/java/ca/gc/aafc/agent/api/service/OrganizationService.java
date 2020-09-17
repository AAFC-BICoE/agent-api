package ca.gc.aafc.agent.api.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import ca.gc.aafc.agent.api.entities.Organization;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DinaService;
import lombok.NonNull;

@Service
public class OrganizationService extends DinaService<Organization> {

  public OrganizationService(@NonNull BaseDAO baseDAO) {
    super(baseDAO);
  }

  @Override
  protected void preCreate(Organization entity) {
    entity.setUuid(UUID.randomUUID());
  }

  @Override
  protected void preDelete(Organization entity) {

  }

  @Override
  protected void preUpdate(Organization entity) {

  }

}
