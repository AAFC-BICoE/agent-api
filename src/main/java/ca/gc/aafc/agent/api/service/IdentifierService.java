package ca.gc.aafc.agent.api.service;

import ca.gc.aafc.agent.api.entities.Identifier;
import ca.gc.aafc.agent.api.validation.PersonIdentifierTypeValueValidator;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import java.util.Map;
import java.util.UUID;

@Service
public class IdentifierService extends DefaultDinaService<Identifier> {

  private final PersonIdentifierTypeValueValidator identifierValidator;

  public IdentifierService(
    @NonNull BaseDAO baseDAO, 
    @NonNull SmartValidator smartValidator,
    @NonNull PersonIdentifierTypeValueValidator identifierValidator
  ) {
    super(baseDAO, smartValidator);
    this.identifierValidator = identifierValidator;
  }

  @Override
  protected void preCreate(Identifier entity) {
    //Give new UUID unless there is already one
    if (entity.getUuid() == null) {
      entity.setUuid(UUID.randomUUID());
    }
  }

  @Override
  public void validateBusinessRules(Identifier entity) {
    identifierValidator.validate(entity, Map.of(entity.getNamespace(), entity.getValue()));
  }

}
