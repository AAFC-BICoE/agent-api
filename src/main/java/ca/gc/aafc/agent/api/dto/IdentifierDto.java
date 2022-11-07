package ca.gc.aafc.agent.api.dto;

import ca.gc.aafc.agent.api.entities.Identifier;
import ca.gc.aafc.dina.dto.RelatedEntity;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;

import java.time.OffsetDateTime;
import java.util.UUID;

@RelatedEntity(Identifier.class)
@JsonApiResource(type = IdentifierDto.TYPENAME)
@TypeName(IdentifierDto.TYPENAME)
@Data
public class IdentifierDto {

  public static final String TYPENAME = "identifier";

  @JsonApiId
  @Id
  @PropertyName("id")
  private UUID uuid;

  private String namespace;
  private String value;
  private String createdBy;
  private OffsetDateTime createdOn;

}
