package ca.gc.aafc.agent.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrganizationNameTranslationDto {
  private String languageCode;
  private String value;
}
