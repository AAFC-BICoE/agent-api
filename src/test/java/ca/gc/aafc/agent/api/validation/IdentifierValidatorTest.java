package ca.gc.aafc.agent.api.validation;

import javax.inject.Inject;

import ca.gc.aafc.agent.api.BaseIntegrationTest;
import ca.gc.aafc.agent.api.entities.Identifier;
import ca.gc.aafc.agent.api.testsupport.factories.IdentifierFactory;
import ca.gc.aafc.dina.validation.ValidationErrorsHelper;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.Errors;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IdentifierValidatorTest extends BaseIntegrationTest {

  @Inject
  private IdentifierValidator identifierValidator;

  @Inject
  private MessageSource messageSource;

  @Test
  void validate_WhenValid_NoErrors() {
    Identifier identifier = IdentifierFactory.newIdentifier()
            .namespace("orcid").build();
    Errors errors = ValidationErrorsHelper.newErrorsObject(identifier.getNamespace(), identifier);
    identifierValidator.validate(identifier, errors);
    assertFalse(errors.hasErrors());
  }

  @Test
  void validate_WhenAssociationTypeNotValid_HasError() {
    String expectedErrorMessage = getExpectedErrorMessage(IdentifierValidator.IDENTIFIER_NAMESPACE_NOT_IN_VOCABULARY);

    Identifier identifier = IdentifierFactory.newIdentifier()
            .namespace("invalid_namespace").build();
    Errors errors = ValidationErrorsHelper.newErrorsObject(identifier.getNamespace(), identifier);

    identifierValidator.validate(identifier, errors);
    assertTrue(errors.hasErrors());
    assertEquals(1, errors.getAllErrors().size());
    assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }

  private String getExpectedErrorMessage(String key) {
    return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
  }
}

