package ca.gc.aafc.agent.api.entities;

import ca.gc.aafc.dina.entity.DinaEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.NaturalId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@RequiredArgsConstructor
@SuppressFBWarnings(justification = "ok for Hibernate Entity", value = { "EI_EXPOSE_REP", "EI_EXPOSE_REP2" })
public class PersonOrganization implements DinaEntity {

  private Integer id;
  private UUID uuid;

  private Person person;
  private Organization organization;

  private String createdBy;
  private OffsetDateTime createdOn;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @NaturalId
  @NotNull
  @Column(name = "uuid", unique = true)
  public UUID getUuid() {
    return uuid;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  @NotNull
  @ManyToOne(cascade = {})
  @JoinColumn(name = "person_id", referencedColumnName = "id")
  public Person getPerson() {
    return person;
  }

  public void setPerson(Person person) {
    this.person = person;
  }

  @NotNull
  @ManyToOne(cascade = {})
  @JoinColumn(name = "organization_id", referencedColumnName = "id")
  public Organization getOrganization() {
    return organization;
  }

  public void setOrganization(Organization organization) {
    this.organization = organization;
  }

  public String toString() {
    return new ToStringBuilder(this).append("id", id).append("uuid", uuid).append("organization", organization)
        .append("person", person).toString();
  }

  @PrePersist
  public void init() {
    this.uuid = UUID.randomUUID();
  }

  @Override
  @Transient
  public String getCreatedBy() {
    return null;
  }

  @Override
  @Transient
  public OffsetDateTime getCreatedOn() {
    return null;
  }
}
