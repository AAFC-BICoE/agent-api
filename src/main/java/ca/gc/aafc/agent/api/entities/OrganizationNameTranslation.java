package ca.gc.aafc.agent.api.entities;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity(name = "org_name_translation")
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
public class OrganizationNameTranslation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank
  private String language;

  @NotBlank
  private String value;

  @ManyToOne(fetch = FetchType.LAZY)
  @NotNull
  private Organization organization;

}
