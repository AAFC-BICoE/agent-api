package ca.gc.aafc.agent.api.validation;


import javax.inject.Named;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import ca.gc.aafc.dina.validation.IdentifierTypeValidator;

@Component
public class AgentIdentifierTypeValidator extends IdentifierTypeValidator {
  public AgentIdentifierTypeValidator(@Named("validationMessageSource") MessageSource messageSource) {
    super(messageSource);
  }


}
