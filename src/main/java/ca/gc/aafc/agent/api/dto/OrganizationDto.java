package ca.gc.aafc.agent.api.dto;

import ca.gc.aafc.agent.api.entities.Organization;
import ca.gc.aafc.agent.api.entities.OrganizationNameTranslation;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.mapper.CustomFieldResolver;
import com.fasterxml.jackson.annotation.JsonInclude;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RelatedEntity(Organization.class)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@Data
@JsonApiResource(type = "organization")
public class OrganizationDto {

  @JsonApiId
  private UUID uuid;

  private String name;
  private String[] aliases;

  private String createdBy;
  private OffsetDateTime createdOn;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<OrganizationNameTranslationDto> nameTranslations;

  @CustomFieldResolver(fieldName = "nameTranslations")
  public static List<OrganizationNameTranslationDto> nameTranslationsToDTO(Organization entity) {
    return entity.getNameTranslations() == null ? null : entity.getNameTranslations()
      .stream()
      .map(translation -> OrganizationNameTranslationDto.builder()
        .languageCode(translation.getLanguageCode())
        .value(translation.getValue()).build())
      .collect(Collectors.toList());
  }

  @CustomFieldResolver(fieldName = "nameTranslations")
  public static List<OrganizationNameTranslation> nameTranslationsToEntity(OrganizationDto dto) {
    return dto.getNameTranslations() == null ? null : dto.getNameTranslations()
      .stream()
      .map(translation -> OrganizationNameTranslation.builder()
        .languageCode(translation.getLanguageCode())
        .value(translation.getValue()).build())
      .collect(Collectors.toList());
  }
}
