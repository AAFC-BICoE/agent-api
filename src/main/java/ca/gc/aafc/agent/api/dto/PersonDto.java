package ca.gc.aafc.agent.api.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import ca.gc.aafc.dina.repository.meta.AttributeMetaInfoProvider;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.ShallowReference;
import org.javers.core.metamodel.annotation.TypeName;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

import ca.gc.aafc.agent.api.entities.Person;
import ca.gc.aafc.dina.dto.RelatedEntity;
import lombok.Data;

@Data
@RelatedEntity(Person.class)
@TypeName(PersonDto.TYPENAME)
@JsonApiTypeForClass(PersonDto.TYPENAME)
public class PersonDto extends AttributeMetaInfoProvider implements ca.gc.aafc.dina.dto.JsonApiResource {

  public static final String TYPENAME = "person";

  @Id
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

  @JsonIgnore
  @ShallowReference
  private List<OrganizationDto> organizations;

  @JsonIgnore
  @ShallowReference
  private List<IdentifierDto> identifiers = List.of();

  @Override
  @JsonIgnore
  public String getJsonApiType() {
    return TYPENAME;
  }

  @Override
  @JsonIgnore
  public UUID getJsonApiId() {
    return uuid;
  }
}
