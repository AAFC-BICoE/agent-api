package ca.gc.aafc.agent.api.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import ca.gc.aafc.agent.api.entities.AgentIdentifierType;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.IdentifierTypeService;
import ca.gc.aafc.dina.util.UUIDHelper;
import ca.gc.aafc.dina.validation.IdentifierTypeValidator;

import lombok.NonNull;

@Service
public class AgentIdentifierTypeService extends IdentifierTypeService<AgentIdentifierType> {

  public AgentIdentifierTypeService(@NonNull BaseDAO baseDAO,
                                    @NonNull SmartValidator validator,
                                    IdentifierTypeValidator identifierTypeValidator) {
    super(baseDAO, validator, identifierTypeValidator);
  }

  @Override
  protected void preCreate(AgentIdentifierType entity) {
    if (entity.getUuid() == null) {
      entity.setUuid(UUIDHelper.generateUUIDv7());
    }
    super.preCreate(entity);
  }
}
