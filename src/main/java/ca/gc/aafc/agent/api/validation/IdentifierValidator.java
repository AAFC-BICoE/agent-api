package ca.gc.aafc.agent.api.validation;

import ca.gc.aafc.agent.api.entities.Identifier;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class IdentifierValidator implements Validator {

  @Override
  public boolean supports(@NonNull Class<?> clazz) {
    return Identifier.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(@NonNull Object target, @NonNull Errors errors) {
    if (!supports(target.getClass())) {
      throw new IllegalArgumentException("IdentifierValidator not supported for class " + target.getClass());
    }
  }
}
