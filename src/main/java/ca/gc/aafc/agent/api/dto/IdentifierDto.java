package ca.gc.aafc.agent.api.dto;

import ca.gc.aafc.agent.api.entities.Identifier;
import ca.gc.aafc.dina.dto.RelatedEntity;
import lombok.Data;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@RelatedEntity(Identifier.class)
@TypeName(IdentifierDto.TYPENAME)
@JsonApiTypeForClass(IdentifierDto.TYPENAME)
public class IdentifierDto implements ca.gc.aafc.dina.dto.JsonApiResource {

  public static final String TYPENAME = "identifier";

  @Id
  @PropertyName("id")
  @com.toedter.spring.hateoas.jsonapi.JsonApiId
  private UUID uuid;

  private String namespace;
  private String value;
  private String createdBy;
  private OffsetDateTime createdOn;

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
