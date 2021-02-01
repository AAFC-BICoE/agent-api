package ca.gc.aafc.agent.api.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import ca.gc.aafc.agent.api.entities.Person;
import ca.gc.aafc.dina.dto.RelatedEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;

@RelatedEntity(Person.class)
@SuppressFBWarnings({ "EI_EXPOSE_REP", "EI_EXPOSE_REP2" })
@Data
@JsonApiResource(type = "person")
public class PersonDto {

  @JsonApiId
  private UUID uuid;
  
  private String displayName;
  private String email;
  private String createdBy;
  private OffsetDateTime createdOn;

  @JsonApiRelation
  private List<OrganizationDto> organizations;  

  private String givenNames;  
  private String familyNames;  
}
