package ca.gc.aafc.agent.api.validation;

import java.util.Map;
import java.util.Objects;

import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;

import ca.gc.aafc.agent.api.entities.AgentControlledVocabularyItem;
import ca.gc.aafc.dina.entity.DinaEntity;
import ca.gc.aafc.dina.service.ControlledVocabularyItemService;
import ca.gc.aafc.dina.validation.BaseControlledVocabularyValueValidator;
import ca.gc.aafc.dina.validation.ValidationErrorsHelper;
import jakarta.inject.Named;
import lombok.NonNull;

import static ca.gc.aafc.agent.api.config.AgentControlledVocabularyConfiguration.IDENTIFIER_TYPE_VOCAB_UUID;

/**
 * Base class for identifier type validator. Concrete class mostly need to add the Component Spring annotation
 * and specify the Dina Component.
 */
public abstract class IdentifierTypeValueValidator extends BaseControlledVocabularyValueValidator<AgentControlledVocabularyItem> {

  public IdentifierTypeValueValidator(@Named("validationMessageSource") MessageSource messageSource,
                                      @NonNull ControlledVocabularyItemService<AgentControlledVocabularyItem> vocabItemService) {
    super(messageSource, vocabItemService);

    
  }

  /**
   * dinaComponent restriction to scope ControlledVocabularyItem.
   *
   * @return
   */
  public abstract String getDinaComponent();

  public <D extends DinaEntity> void validate(D entity, Map<String, String> identifiers) {
    validate(identifiers, ValidationErrorsHelper.newErrorsObject(entity));
  }

  public void validate(String objIdentifier, Object target, Map<String, String> identifiers) {
    Objects.requireNonNull(target);
    validate(identifiers, ValidationErrorsHelper.newErrorsObject(objIdentifier, target));
  }

  /**
   * Internal validate method that is throwing {@link javax.validation.ValidationException} if there is
   * any errors
   *
   * @param identifiers
   * @param errors
   */
  private void validate(Map<String, String> identifiers, Errors errors) {
    validateItems(identifiers,
      () -> vocabItemService.findAllByKeys(identifiers.keySet(), IDENTIFIER_TYPE_VOCAB_UUID,
        getDinaComponent()), errors);
    ValidationErrorsHelper.errorsToValidationException(errors);
  }
}
