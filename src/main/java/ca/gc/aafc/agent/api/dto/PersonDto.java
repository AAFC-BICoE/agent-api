package ca.gc.aafc.agent.api.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import ca.gc.aafc.dina.repository.meta.AttributeMetaInfoProvider;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.ShallowReference;
import org.javers.core.metamodel.annotation.TypeName;

import ca.gc.aafc.agent.api.entities.Person;
import ca.gc.aafc.dina.dto.RelatedEntity;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;

@Data
@RelatedEntity(Person.class)
@TypeName(PersonDto.TYPENAME)
@JsonApiResource(type = PersonDto.TYPENAME)
public class PersonDto extends AttributeMetaInfoProvider {

  public static final String TYPENAME = "person";

  @Id
  @JsonApiId
  @PropertyName("id")
  @com.toedter.spring.hateoas.jsonapi.JsonApiId
  private UUID uuid;
  
  private String displayName;
  private String email;
  private String createdBy;
  private OffsetDateTime createdOn;

  private String givenNames;  
  private String familyNames;
  private String[] aliases;
  private String webpage;
  private String remarks;

  @JsonApiRelation
  @ShallowReference
  private List<OrganizationDto> organizations;

  @JsonApiRelation
  @ShallowReference
  private List<IdentifierDto> identifiers = List.of();
}
