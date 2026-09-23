package ca.gc.aafc.agent.api.validation;

import jakarta.inject.Named;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import ca.gc.aafc.dina.validation.ControlledVocabularyItemValidator;

@Component
public class AgentControlledVocabularyItemValidator extends ControlledVocabularyItemValidator {
  public AgentControlledVocabularyItemValidator(@Named("validationMessageSource") MessageSource messageSource) {
    super(messageSource);
  }
}
