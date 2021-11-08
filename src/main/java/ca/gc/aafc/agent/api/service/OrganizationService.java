package ca.gc.aafc.agent.api.service;

import ca.gc.aafc.agent.api.entities.Organization;
import ca.gc.aafc.agent.api.validation.OrganizationValidator;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import java.util.UUID;

@Service
public class OrganizationService extends DefaultDinaService<Organization> {

  private final OrganizationValidator organizationValidator;

  public OrganizationService(
    @NonNull BaseDAO baseDAO, 
    @NonNull SmartValidator smartValidator,
    @NonNull OrganizationValidator organizationValidator) {
    super(baseDAO, smartValidator);
    this.organizationValidator = organizationValidator;
  }

  @Override
  protected void preCreate(Organization entity) {
    //Give new Organization UUID
    entity.setUuid(UUID.randomUUID());
  }

  @Override
  public void validateBusinessRules(Organization entity) {
    applyBusinessRule(entity, organizationValidator);
  }

}
