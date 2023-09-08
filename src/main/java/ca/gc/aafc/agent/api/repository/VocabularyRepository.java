package ca.gc.aafc.agent.api.repository;

import org.springframework.stereotype.Repository;

import ca.gc.aafc.agent.api.config.AgentVocabularyConfiguration;
import ca.gc.aafc.agent.api.dto.VocabularyDto;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ReadOnlyResourceRepositoryBase;
import io.crnk.core.resource.list.ResourceList;
import lombok.NonNull;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class VocabularyRepository extends ReadOnlyResourceRepositoryBase<VocabularyDto, String> {
  
  private final List<VocabularyDto> vocabulary;

  protected VocabularyRepository(
    @NonNull AgentVocabularyConfiguration collectionVocabularyConfiguration) {
    super(VocabularyDto.class);

    vocabulary = collectionVocabularyConfiguration.getVocabulary()
        .entrySet()
        .stream()
        .map( entry -> new VocabularyDto(entry.getKey(), entry.getValue()))
        .collect( Collectors.toList());
  }

  @Override
  public ResourceList<VocabularyDto> findAll(QuerySpec querySpec) {
    return querySpec.apply(vocabulary);
  }
}
