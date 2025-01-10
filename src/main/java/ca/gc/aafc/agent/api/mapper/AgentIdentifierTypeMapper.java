package ca.gc.aafc.agent.api.mapper;

import java.util.Set;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.agent.api.dto.AgentIdentifierTypeDto;
import ca.gc.aafc.agent.api.entities.AgentIdentifierType;
import ca.gc.aafc.dina.mapper.DinaMapperV2;

@Mapper
public interface AgentIdentifierTypeMapper extends DinaMapperV2<AgentIdentifierTypeDto, AgentIdentifierType> {

  AgentIdentifierTypeMapper INSTANCE = Mappers.getMapper(AgentIdentifierTypeMapper.class);

  AgentIdentifierTypeDto toDto(AgentIdentifierType entity, @Context Set<String> provided, @Context String scope);

  AgentIdentifierType toEntity(AgentIdentifierTypeDto dto, @Context Set<String> provided, @Context String scope);

}
