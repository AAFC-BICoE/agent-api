package ca.gc.aafc.agent.api.dto;

import ca.gc.aafc.agent.api.entities.Organization;
import ca.gc.aafc.agent.api.entities.OrganizationNameTranslation;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.mapper.CustomFieldAdapter;
import ca.gc.aafc.dina.mapper.DinaFieldAdapter;
import ca.gc.aafc.dina.mapper.IgnoreDinaMapping;
import com.fasterxml.jackson.annotation.JsonInclude;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@RelatedEntity(Organization.class)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@Data
@JsonApiResource(type = "organization")
@CustomFieldAdapter(adapters = OrganizationDto.NamesAdapter.class)
public class OrganizationDto {

  @JsonApiId
  private UUID uuid;

  private String[] aliases;

  private String createdBy;
  private OffsetDateTime createdOn;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  @IgnoreDinaMapping(reason = "custom mapped")
  private List<OrganizationNameTranslationDto> names;

  public static class NamesAdapter
    implements DinaFieldAdapter<
    OrganizationDto,
    Organization,
    List<OrganizationNameTranslationDto>,
    List<OrganizationNameTranslation>> {

    @Override
    public List<OrganizationNameTranslationDto> toDTO(List<OrganizationNameTranslation> names) {
      return names == null ? null : names.stream()
        .map(translation -> OrganizationNameTranslationDto.builder()
          .languageCode(translation.getLanguageCode())
          .name(translation.getName()).build())
        .collect(Collectors.toList());
    }

    @Override
    public List<OrganizationNameTranslation> toEntity(List<OrganizationNameTranslationDto> names) {
      return names == null ? null : names.stream()
        .map(translation -> OrganizationNameTranslation.builder()
          .languageCode(translation.getLanguageCode())
          .name(translation.getName()).build())
        .collect(Collectors.toList());
    }

    @Override
    public Consumer<List<OrganizationNameTranslation>> entityApplyMethod(Organization entityRef) {
      return entityRef::setNames;
    }

    @Override
    public Consumer<List<OrganizationNameTranslationDto>> dtoApplyMethod(OrganizationDto dtoRef) {
      return dtoRef::setNames;
    }

    @Override
    public Supplier<List<OrganizationNameTranslation>> entitySupplyMethod(Organization entityRef) {
      return entityRef::getNames;
    }

    @Override
    public Supplier<List<OrganizationNameTranslationDto>> dtoSupplyMethod(OrganizationDto dtoRef) {
      return dtoRef::getNames;
    }
  }

}
