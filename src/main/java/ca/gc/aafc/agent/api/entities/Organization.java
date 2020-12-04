package ca.gc.aafc.agent.api.entities;

import ca.gc.aafc.agent.api.dto.OrganizationDto;
import ca.gc.aafc.dina.entity.DinaEntity;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@TypeDef(name = "string-array", typeClass = StringArrayType.class)
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
@SuppressFBWarnings(
  justification = "ok for Hibernate Entity",
  value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@NaturalIdCache
public class Organization implements DinaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NaturalId
  @NotNull
  @Column(name = "uuid", unique = true)
  private UUID uuid;

  @Type(type = "string-array")
  private String[] aliases;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "created_on", insertable = false, updatable = false)
  @Generated(value = GenerationTime.INSERT)
  private OffsetDateTime createdOn;

  @ManyToMany(mappedBy = "organizations", fetch = FetchType.LAZY)
  @ToString.Exclude
  private List<Person> persons;

  @OneToMany(
    mappedBy = "organization",
    cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
    fetch = FetchType.EAGER)
  @ToString.Exclude
  private List<OrganizationNameTranslation> names;

  public static List<OrganizationNameTranslation> nameTranslationsToEntity(OrganizationDto dto) {
    return dto.getNames() == null ? null : dto.getNames()
      .stream()
      .map(translation -> OrganizationNameTranslation.builder()
        .languageCode(translation.getLanguageCode())
        .name(translation.getName()).build())
      .collect(Collectors.toList());
  }
}
