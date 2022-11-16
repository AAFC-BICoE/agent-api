package ca.gc.aafc.agent.api.validation;

import javax.inject.Inject;

import ca.gc.aafc.agent.api.BaseIntegrationTest;
import ca.gc.aafc.agent.api.entities.Identifier;
import ca.gc.aafc.dina.validation.ValidationErrorsHelper;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.Errors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IdentifierValidatorTest extends BaseIntegrationTest {
  @Inject
  private IdentifierValidator identifierValidator;

  @Inject
  private MessageSource messageSource;
  @Test
  void validate_WhenValid_NoErrors() {
    Identifier identifier = newIdentifier();
    identifier.setNamespace("orcid");
    Errors errors = ValidationErrorsHelper.newErrorsObject(identifier.getNamespace(), identifier);
    identifierValidator.validate(identifier, errors);
    Assertions.assertFalse(errors.hasErrors());
  }

  @Test
  void validate_WhenAssociationTypeNotValid_HasError() {
    String expectedErrorMessage = getExpectedErrorMessage(IdentifierValidator.IDENTIFIER_NAMESPACE_NOT_IN_VOCABULARY);

    Identifier identifier = newIdentifier();
    identifier.setNamespace("invalid_namespace");

    Errors errors = ValidationErrorsHelper.newErrorsObject(identifier.getNamespace(), identifier);

    identifierValidator.validate(identifier, errors);
    Assertions.assertTrue(errors.hasErrors());
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }

  private static Identifier newIdentifier() {
    return Identifier.builder()
      .build();
  }

  private String getExpectedErrorMessage(String key) {
    return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
  }
}

