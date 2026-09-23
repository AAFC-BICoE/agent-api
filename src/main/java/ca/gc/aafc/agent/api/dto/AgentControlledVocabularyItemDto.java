package ca.gc.aafc.agent.api.dto;

import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.ShallowReference;
import org.javers.core.metamodel.annotation.TypeName;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

import ca.gc.aafc.agent.api.entities.AgentControlledVocabularyItem;
import ca.gc.aafc.dina.dto.BaseControlledVocabularyItemDto;
import ca.gc.aafc.dina.dto.RelatedEntity;

import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@RelatedEntity(AgentControlledVocabularyItem.class)
@JsonApiTypeForClass(BaseControlledVocabularyItemDto.TYPENAME)
@Data
@TypeName(BaseControlledVocabularyItemDto.TYPENAME)
public class AgentControlledVocabularyItemDto extends BaseControlledVocabularyItemDto<AgentControlledVocabularyDto> {

  private AgentControlledVocabularyDto controlledVocabulary;

  @JsonApiId
  @Id
  @PropertyName("id")
  public UUID getUuid() {
    return uuid;
  }

  @Override
  @JsonIgnore
  @ShallowReference
  public AgentControlledVocabularyDto getControlledVocabulary() {
    return controlledVocabulary;
  }
}
