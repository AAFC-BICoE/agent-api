package ca.gc.aafc.agent.api.dto;

import ca.gc.aafc.agent.api.entities.Organization;
import ca.gc.aafc.agent.api.entities.OrganizationNameTranslation;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.mapper.CustomFieldResolver;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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

  private List<OrganizationNameTranslation> nameTranslations;

  @CustomFieldResolver(fieldName = "nameTranslations")
  private static List<OrganizationNameTranslation> toDTO(Organization entity) {
    return Collections.emptyList();
  }

  @CustomFieldResolver(fieldName = "nameTranslations")
  private static List<OrganizationNameTranslation> toEntity(OrganizationDto dto) {
    return Collections.emptyList();
  }

}
