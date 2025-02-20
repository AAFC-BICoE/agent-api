package ca.gc.aafc.agent.api.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.agent.api.dto.PersonDto;
import ca.gc.aafc.agent.api.entities.Person;
import ca.gc.aafc.dina.mapper.DinaMapperV2;

import java.util.Set;

@Mapper
public interface PersonMapper extends DinaMapperV2<PersonDto, Person> {

  PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

  PersonDto toDto(Person entity, @Context Set<String> provided, @Context String scope);

  Person toEntity(PersonDto dto, @Context Set<String> provided, @Context String scope);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patchEntity(@MappingTarget Person entity, PersonDto dto, @Context Set<String> provided, @Context String scope);

}
