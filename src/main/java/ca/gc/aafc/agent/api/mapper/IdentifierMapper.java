package ca.gc.aafc.agent.api.mapper;

import java.util.Set;

import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.agent.api.dto.IdentifierDto;
import ca.gc.aafc.agent.api.entities.Identifier;
import ca.gc.aafc.dina.mapper.DinaMapperV2;

@Mapper
public interface IdentifierMapper extends DinaMapperV2<IdentifierDto, Identifier> {

  IdentifierMapper INSTANCE = Mappers.getMapper(IdentifierMapper.class);

  IdentifierDto toDto(Identifier entity, @Context Set<String> provided, @Context String scope);

  Identifier toEntity(IdentifierDto dto, @Context Set<String> provided, @Context String scope);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patchEntity(@MappingTarget Identifier entity, IdentifierDto dto, @Context Set<String> provided, @Context String scope);

}

