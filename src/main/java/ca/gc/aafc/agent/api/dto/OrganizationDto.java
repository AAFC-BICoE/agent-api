package ca.gc.aafc.agent.api.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;

import ca.gc.aafc.agent.api.entities.Organization;
import ca.gc.aafc.agent.api.entities.OrganizationName;
import ca.gc.aafc.dina.dto.RelatedEntity;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;

@RelatedEntity(Organization.class)
@Data
@JsonApiResource(type = OrganizationDto.TYPENAME)
@TypeName(OrganizationDto.TYPENAME)
public class OrganizationDto {

  public static final String TYPENAME = "organization";

  @JsonApiId
  @Id
  @PropertyName("id")
  private UUID uuid;

  private String[] aliases;

  private String createdBy;
  private OffsetDateTime createdOn;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<OrganizationName> names;

}
