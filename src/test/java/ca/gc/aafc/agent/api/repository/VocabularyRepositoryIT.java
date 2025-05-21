package ca.gc.aafc.agent.api.repository;

import ca.gc.aafc.agent.api.BaseIntegrationTest;
import ca.gc.aafc.agent.api.dto.VocabularyDto;
import ca.gc.aafc.agent.api.config.AgentVocabularyConfiguration;
import ca.gc.aafc.dina.vocabulary.VocabularyElementConfiguration;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VocabularyRepositoryIT extends BaseIntegrationTest {

  @Inject
  private VocabularyRepository vocabularyConfigurationRepository;

  @Inject
  private AgentVocabularyConfiguration vocabularyConfiguration;

  @Test
  public void findAll_VocabularyConfiguration() {
    List<VocabularyDto> listOfVocabularies =
      vocabularyConfigurationRepository.findAll(null);
    assertEquals(1, listOfVocabularies.size());

    List<List<VocabularyElementConfiguration>> listOfVocabularyElements = new ArrayList<>();
    for (VocabularyDto vocabularyDto : listOfVocabularies) {
      listOfVocabularyElements.add(vocabularyDto.getVocabularyElements());
    }

    MatcherAssert.assertThat(
      listOfVocabularyElements,
      Matchers.containsInAnyOrder(
        vocabularyConfiguration.getVocabulary().get("identifiers")
      ));
  }

}

