package ca.gc.aafc.agent.api.dto;

import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;

import ca.gc.aafc.agent.api.entities.AgentIdentifierType;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.i18n.MultilingualTitle;

import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@RelatedEntity(AgentIdentifierType.class)
@JsonApiResource(type = AgentIdentifierTypeDto.TYPENAME)
@TypeName(AgentIdentifierTypeDto.TYPENAME)
@Data
public class AgentIdentifierTypeDto {

  public static final String TYPENAME = "identifier-type";

  @JsonApiId
  @Id
  @PropertyName("id")
  private UUID uuid;

  private String key;
  private String name;
  private List<String> dinaComponents;
  private String uriTemplate;
  private String term;
  private MultilingualTitle multilingualTitle;

  private String createdBy;
  private OffsetDateTime createdOn;

}
