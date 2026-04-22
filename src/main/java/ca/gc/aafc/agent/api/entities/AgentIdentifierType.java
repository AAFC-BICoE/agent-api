package ca.gc.aafc.agent.api.entities;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
import io.hypersistence.utils.hibernate.type.json.JsonType;

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

  //@Type(type = "list-array")
  private List<String> dinaComponents;

  @Size(max = 100)
  private String uriTemplate;

  @Size(max = 100)
  private String term;

  @Type(JsonType.class)
  @Column(columnDefinition = "jsonb")
  private MultilingualTitle multilingualTitle;

}
