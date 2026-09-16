package ca.gc.aafc.agent.api.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.agent.api.dto.AgentControlledVocabularyDto;
import ca.gc.aafc.agent.api.entities.AgentControlledVocabulary;
import ca.gc.aafc.dina.mapper.DinaMapperV2;

import java.util.Set;

@Mapper
public interface AgentControlledVocabularyMapper extends DinaMapperV2<AgentControlledVocabularyDto, AgentControlledVocabulary> {

  AgentControlledVocabularyMapper INSTANCE = Mappers.getMapper(AgentControlledVocabularyMapper.class);

  AgentControlledVocabularyDto toDto(AgentControlledVocabulary entity, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  AgentControlledVocabulary toEntity(AgentControlledVocabularyDto dto, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patchEntity(@MappingTarget AgentControlledVocabulary entity, AgentControlledVocabularyDto dto, @Context Set<String> provided, @Context String scope);

}
