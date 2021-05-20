package ca.gc.aafc.agent.api.dto;

import org.javers.core.metamodel.annotation.Value;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Value // This class is considered a "value" belonging to an OrganizationDto.
public class OrganizationNameTranslationDto {
  private String languageCode;
  private String name;
}
