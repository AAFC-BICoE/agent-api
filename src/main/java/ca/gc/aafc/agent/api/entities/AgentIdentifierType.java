package ca.gc.aafc.agent.api.entities;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.annotations.Type;

import ca.gc.aafc.dina.entity.IdentifierType;
import ca.gc.aafc.dina.i18n.MultilingualTitle;

@Entity(name = "identifier_type")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@NaturalIdCache
public class AgentIdentifierType implements IdentifierType {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NaturalId
  @NotNull
  @Column(name = "uuid", unique = true)
  private UUID uuid;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "created_on", insertable = false, updatable = false)
  @Generated(value = GenerationTime.INSERT)
  private OffsetDateTime createdOn;

  @Size(max = 50)
  @Column(name = "key")
  private String key;

  @Size(max = 50)
  private String name;

  @Type(type = "list-array")
  private List<String> dinaComponents;

  @Size(max = 100)
  private String uriTemplate;

  @Size(max = 100)
  private String term;

  @Type(type = "jsonb")
  private MultilingualTitle multilingualTitle;

}
