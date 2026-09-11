package ca.gc.aafc.agent.api.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.agent.api.dto.AgentControlledVocabularyItemDto;
import ca.gc.aafc.agent.api.entities.AgentControlledVocabularyItem;
import ca.gc.aafc.dina.mapper.DinaMapperV2;


import java.util.Set;

@Mapper
public interface AgentControlledVocabularyItemMapper extends DinaMapperV2<AgentControlledVocabularyItemDto, AgentControlledVocabularyItem> {

  AgentControlledVocabularyItemMapper INSTANCE = Mappers.getMapper(AgentControlledVocabularyItemMapper.class);

  AgentControlledVocabularyItemDto toDto(AgentControlledVocabularyItem entity, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "controlledVocabulary", ignore = true)
  AgentControlledVocabularyItem toEntity(AgentControlledVocabularyItemDto dto, @Context Set<String> provided, @Context String scope);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "controlledVocabulary", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patchEntity(@MappingTarget AgentControlledVocabularyItem entity, AgentControlledVocabularyItemDto dto, @Context Set<String> provided, @Context String scope);
}
