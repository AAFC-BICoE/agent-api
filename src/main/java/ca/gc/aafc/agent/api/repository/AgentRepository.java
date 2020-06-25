package ca.gc.aafc.agent.api.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import ca.gc.aafc.agent.api.dto.AgentDto;
import ca.gc.aafc.agent.api.entities.Agent;
import ca.gc.aafc.dina.filter.DinaFilterResolver;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.service.DinaService;
import lombok.NonNull;

@Repository
public class AgentRepository extends DinaRepository<AgentDto, Agent> {

  // Bean does not exist with keycloak disabled.
  private Optional<DinaAuthenticatedUser> authenticatedUser;

  public AgentRepository(
    @NonNull DinaService<Agent> dinaService,
    @NonNull DinaFilterResolver filterResolver,
    Optional<DinaAuthenticatedUser> authenticatedUser
  ) {
    super(
      dinaService,
      new DinaMapper<>(AgentDto.class),
      AgentDto.class,
      Agent.class,
      filterResolver);
    this.authenticatedUser = authenticatedUser;
  }

  @Override
  public <S extends AgentDto> S create(S resource) {
    if (authenticatedUser.isPresent()) {
      resource.setCreatedBy(authenticatedUser.get().getUsername());
    }
    return super.create(resource);
  }

}
