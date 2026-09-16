package ca.gc.aafc.agent.api.testsupport.fixtures;

import org.apache.commons.lang3.RandomStringUtils;

import ca.gc.aafc.agent.api.config.AgentControlledVocabularyConfiguration;
import ca.gc.aafc.agent.api.dto.AgentControlledVocabularyItemDto;
import ca.gc.aafc.dina.vocabulary.TypedVocabularyElement;

public class AgentControlledVocabularyItemTestFixture {

    public static final String GROUP = "dina";

    public static AgentControlledVocabularyItemDto newAgentControlledVocabularyItem() {
        AgentControlledVocabularyItemDto agentControlledVocabularyItemDto = new AgentControlledVocabularyItemDto();
        agentControlledVocabularyItemDto.setName(RandomStringUtils.randomAlphabetic(5));
        agentControlledVocabularyItemDto.setVocabularyElementType(
                TypedVocabularyElement.VocabularyElementType.INTEGER);
        agentControlledVocabularyItemDto.setAcceptedValues(new String[] { "1", "2" });
        agentControlledVocabularyItemDto.setTerm("the-term");
        agentControlledVocabularyItemDto.setUnit("cm");
        agentControlledVocabularyItemDto.setCreatedBy("created by");
        agentControlledVocabularyItemDto.setGroup("test");
        agentControlledVocabularyItemDto.setUriTemplate("http://test.org/$1");
        agentControlledVocabularyItemDto
                .setDinaComponent(AgentControlledVocabularyConfiguration.DinaComponent.PERSON.name());
        agentControlledVocabularyItemDto.setMultilingualTitle(MultilingualTestFixture.newMultilingualTitle());
        agentControlledVocabularyItemDto
                .setMultilingualDescription(MultilingualTestFixture.newMultilingualDescription());
        return agentControlledVocabularyItemDto;
    }

}
