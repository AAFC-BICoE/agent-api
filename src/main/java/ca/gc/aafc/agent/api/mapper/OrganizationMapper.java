package ca.gc.aafc.agent.api.mapper;

import java.util.Set;

import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.agent.api.dto.OrganizationDto;
import ca.gc.aafc.agent.api.entities.Organization;
import ca.gc.aafc.dina.mapper.DinaMapperV2;

@Mapper
public interface OrganizationMapper extends DinaMapperV2<OrganizationDto, Organization> {

  OrganizationMapper INSTANCE = Mappers.getMapper(OrganizationMapper.class);

  OrganizationDto toDto(Organization entity, @Context Set<String> provided, @Context String scope);

  Organization toEntity(OrganizationDto dto, @Context Set<String> provided, @Context String scope);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patchEntity(@MappingTarget Organization entity, OrganizationDto dto, @Context Set<String> provided, @Context String scope);

}

