package ca.gc.aafc.agent.api.validation;

import ca.gc.aafc.agent.api.entities.Identifier;
import ca.gc.aafc.agent.api.vocabulary.AgentVocabularyConfiguration;
import ca.gc.aafc.dina.vocabulary.VocabularyConfiguration.VocabularyElement;
import lombok.NonNull;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

@Component
public class IdentifierValidator implements Validator {

  public static final String IDENTIFIER_NAMESPACE_NOT_IN_VOCABULARY = "validation.constraint.violation.namespaceNotInVocabulary";

  private final MessageSource messageSource;
  private final List<VocabularyElement> identifiersVocabulary;

  public IdentifierValidator(
    MessageSource messageSource, 
    AgentVocabularyConfiguration vocabularyConfiguration
  ) {
    this.messageSource = messageSource;
    identifiersVocabulary = vocabularyConfiguration.getVocabulary().get(AgentVocabularyConfiguration.IDENTIFIERS);
  }

  @Override
  public boolean supports(@NonNull Class<?> clazz) {
    return Identifier.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(@NonNull Object target, @NonNull Errors errors) {
    if (!supports(target.getClass())) {
      throw new IllegalArgumentException("IdentifierValidator not supported for class " + target.getClass());
    }
    Identifier identifier = (Identifier) target;
    validateIdentifierNamespace(errors, identifier);
  }

  private void validateIdentifierNamespace(Errors errors, Identifier identifier) {
    if (StringUtils.isNotBlank(identifier.getNamespace())) {
      Optional<VocabularyElement> foundNamespace = identifiersVocabulary
          .stream().filter(o -> o.getName().equalsIgnoreCase(identifier.getNamespace())).findFirst();
      if (foundNamespace.isPresent()) {
        identifier.setNamespace(foundNamespace.get().getName());
      } else {
        String errorMessage = getMessage(IDENTIFIER_NAMESPACE_NOT_IN_VOCABULARY);
        errors.rejectValue("namespace", IDENTIFIER_NAMESPACE_NOT_IN_VOCABULARY, errorMessage);
      }
    }
  }

  private String getMessage(String key) {
    return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
  }
}
