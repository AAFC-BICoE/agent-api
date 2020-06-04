package ca.gc.aafc.agent.api.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import ca.gc.aafc.agent.api.entities.Agent;
import ca.gc.aafc.dina.dto.RelatedEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;

@RelatedEntity(Agent.class)
@SuppressFBWarnings({ "EI_EXPOSE_REP", "EI_EXPOSE_REP2" })
@Data
@JsonApiResource(type = "agent")
public class AgentDto {

  @JsonApiId
  private UUID uuid;

  private String displayName;
  private String email;
  private String createdBy;
  private OffsetDateTime createdOn;
}
