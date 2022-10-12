package ca.gc.aafc.agent.api.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import ca.gc.aafc.dina.repository.meta.AttributeMetaInfoProvider;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;

import ca.gc.aafc.agent.api.entities.Identifier;
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
@JsonApiResource(type = PersonDto.TYPENAME)
@TypeName(PersonDto.TYPENAME)
public class PersonDto extends AttributeMetaInfoProvider {

  public static final String TYPENAME = "person";

  @JsonApiId
  @Id
  @PropertyName("id")
  private UUID uuid;
  
  private String displayName;
  private String email;
  private String createdBy;
  private OffsetDateTime createdOn;

  @JsonApiRelation
  private List<OrganizationDto> organizations;  

  private String givenNames;  
  private String familyNames;  

  private String[] aliases;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<Identifier> identifiers = List.of();

  private String webpage;

  private String remarks;
}
