package ca.gc.aafc.agent.api.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import ca.gc.aafc.agent.api.dto.IdentifierDto;
import ca.gc.aafc.agent.api.dto.PersonDto;
import ca.gc.aafc.agent.api.entities.Identifier;
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


  // --- Relationships handling ---
  /**
   * Default method to intercept the mapping and set the context to the relationship
   * @param dto
   * @param provided
   * @param scope will be ignored but required so MapStruct uses it
   * @return
   */
  default Identifier toEntity(IdentifierDto dto, @Context Set<String> provided, @Context String scope) {
    return toIdentifierEntity(dto, provided, "identifiers");
  }

  /**
   * Default method to intercept the mapping and set the context to the relationship
   * @param dto
   * @param provided
   * @param scope will be ignored but required so MapStruct uses it
   * @return
   */
  default IdentifierDto toDto(Identifier dto, @Context Set<String> provided, @Context String scope) {
    return toIdentifierDto(dto, provided, "identifiers");
  }

  Identifier toIdentifierEntity(IdentifierDto dto, Set<String> provided, String scope);
  IdentifierDto toIdentifierDto(Identifier entity, Set<String> provided, String scope);

}
