package ca.gc.aafc.agent.api.service;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import ca.gc.aafc.agent.api.config.AgentVocabularyConfiguration;
import ca.gc.aafc.agent.api.dto.VocabularyDto;
import ca.gc.aafc.dina.service.CollectionBackedReadOnlyDinaService;

@Service
public class VocabularyService extends CollectionBackedReadOnlyDinaService<String, VocabularyDto> {

  public VocabularyService(AgentVocabularyConfiguration agentVocabularyConfiguration) {
    super(agentVocabularyConfiguration.getVocabulary()
      .entrySet()
      .stream()
      .map(entry -> new VocabularyDto(entry.getKey(), entry.getValue()))
      .collect(Collectors.toList()), VocabularyDto::getId);
  }
}
