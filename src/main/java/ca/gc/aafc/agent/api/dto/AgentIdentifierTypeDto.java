package ca.gc.aafc.agent.api.dto;

import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;

import ca.gc.aafc.agent.api.entities.AgentIdentifierType;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.i18n.MultilingualTitle;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Data;

import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

@RelatedEntity(AgentIdentifierType.class)
@JsonApiTypeForClass(AgentIdentifierTypeDto.TYPENAME)
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
