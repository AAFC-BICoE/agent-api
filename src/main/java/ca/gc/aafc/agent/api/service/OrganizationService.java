package ca.gc.aafc.agent.api.service;

import ca.gc.aafc.agent.api.entities.Organization;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import java.util.UUID;

@Service
public class OrganizationService extends DefaultDinaService<Organization> {

  public OrganizationService(@NonNull BaseDAO baseDAO, @NonNull SmartValidator smartValidator) {
    super(baseDAO, smartValidator);
  }

  @Override
  protected void preCreate(Organization entity) {
    //Give new Organization UUID
    entity.setUuid(UUID.randomUUID());
  }

}
