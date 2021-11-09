package ca.gc.aafc.agent.api.validation;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ca.gc.aafc.agent.api.entities.Organization;
import lombok.NonNull;

@Component
public class OrganizationValidator implements Validator {

  private final MessageSource messageSource;
  
  public static final String VALID_REQUIRED_NAME = "organization.constraint.required.name";

  public OrganizationValidator(MessageSource messageSource) {
    this.messageSource = messageSource;
  }
  
  @Override
  public boolean supports(@NonNull Class<?> clazz) {
    return Organization.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(@NonNull Object target, @NonNull Errors errors) {
    if (!supports(target.getClass())) {
      throw new IllegalArgumentException("OrganizationValidator not supported for class " + target.getClass());
    }
    Organization organization = (Organization) target;
    checkHasName(errors, organization);
  }

  private void checkHasName(Errors errors, Organization organization) {
    if (CollectionUtils.isEmpty(organization.getNames())) {
      String errorMessage = getMessage(VALID_REQUIRED_NAME);
      errors.rejectValue("names", VALID_REQUIRED_NAME, errorMessage);
    }
  }

  private String getMessage(String key) {
    return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
  }
  
}
