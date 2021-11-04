package ca.gc.aafc.agent.api.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;

import ca.gc.aafc.agent.api.entities.Organization;
import ca.gc.aafc.agent.api.entities.OrganizationName;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.mapper.CustomFieldAdapter;
import ca.gc.aafc.dina.mapper.DinaFieldAdapter;
import ca.gc.aafc.dina.mapper.IgnoreDinaMapping;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;

@RelatedEntity(Organization.class)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@Data
@JsonApiResource(type = OrganizationDto.TYPENAME)
@CustomFieldAdapter(adapters = OrganizationDto.NamesAdapter.class)
@TypeName(OrganizationDto.TYPENAME)
public class OrganizationDto {

  public static final String TYPENAME = "organization";

  @JsonApiId
  @Id
  @PropertyName("id")
  private UUID uuid;

  private String[] aliases;

  private String createdBy;
  private OffsetDateTime createdOn;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  @IgnoreDinaMapping(reason = "custom mapped")
  private List<OrganizationName> names;

  public static class NamesAdapter
    implements DinaFieldAdapter<
    OrganizationDto,
    Organization,
    List<OrganizationName>,
    List<OrganizationName>> {

    @Override
    public List<OrganizationName> toDTO(List<OrganizationName> names) {
      return names == null ? null : names.stream()
        .map(translation -> OrganizationName.builder()
          .languageCode(translation.getLanguageCode())
          .name(translation.getName()).build())
        .collect(Collectors.toList());
    }

    @Override
    public List<OrganizationName> toEntity(List<OrganizationName> names) {
      return names == null ? null : names.stream()
        .map(translation -> OrganizationName.builder()
          .languageCode(translation.getLanguageCode())
          .name(translation.getName()).build())
        .collect(Collectors.toList());
    }

    @Override
    public Consumer<List<OrganizationName>> entityApplyMethod(Organization entityRef) {
      return entityRef::setNames;
    }

    @Override
    public Consumer<List<OrganizationName>> dtoApplyMethod(OrganizationDto dtoRef) {
      return dtoRef::setNames;
    }

    @Override
    public Supplier<List<OrganizationName>> entitySupplyMethod(Organization entityRef) {
      return entityRef::getNames;
    }

    @Override
    public Supplier<List<OrganizationName>> dtoSupplyMethod(OrganizationDto dtoRef) {
      return dtoRef::getNames;
    }
  }

}
