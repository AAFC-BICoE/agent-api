package ca.gc.aafc.agent.api.dto;

import ca.gc.aafc.dina.vocabulary.VocabularyElementConfiguration;

import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@JsonApiResource(type = "vocabulary")
public class VocabularyDto {
  
  @JsonApiId
  private final String id;

  private final List<VocabularyElementConfiguration> vocabularyElements;

}
