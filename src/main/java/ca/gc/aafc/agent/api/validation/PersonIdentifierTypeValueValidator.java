package ca.gc.aafc.agent.api.validation;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import ca.gc.aafc.agent.api.config.AgentControlledVocabularyConfiguration;
import ca.gc.aafc.agent.api.entities.AgentControlledVocabularyItem;
import ca.gc.aafc.dina.service.ControlledVocabularyItemService;
import jakarta.inject.Named;
import lombok.NonNull;

/**
 * Specific for PERSON usage
 */
@Component
public class PersonIdentifierTypeValueValidator extends IdentifierTypeValueValidator {

  public PersonIdentifierTypeValueValidator (
    @Named("validationMessageSource") MessageSource messageSource,
    @NonNull ControlledVocabularyItemService<AgentControlledVocabularyItem> vocabItemService) {
    super(messageSource, vocabItemService);
  }

  @Override
  public String getDinaComponent() {
    return AgentControlledVocabularyConfiguration.DinaComponent.PERSON.name();
  }
}
