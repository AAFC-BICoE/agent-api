package ca.gc.aafc.agent.api.repository;

import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.gc.aafc.agent.api.dto.AgentIdentifierTypeDto;
import ca.gc.aafc.agent.api.entities.AgentIdentifierType;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.security.auth.DinaAdminCUDAuthorizationService;
import ca.gc.aafc.dina.service.AuditService;
import ca.gc.aafc.dina.service.DinaService;

import java.util.Optional;
import lombok.NonNull;

@Repository
public class AgentIdentifierTypeRepository extends DinaRepository<AgentIdentifierTypeDto, AgentIdentifierType> {

  // Bean does not exist with keycloak disabled.
  private final DinaAuthenticatedUser authenticatedUser;

  public AgentIdentifierTypeRepository(
    @NonNull DinaService<AgentIdentifierType> dinaService,
    @NonNull DinaAdminCUDAuthorizationService authorizationService,
    Optional<DinaAuthenticatedUser> authenticatedUser,
    @NonNull BuildProperties props,
    @NonNull AuditService auditService,
    @NonNull ObjectMapper objMapper
  ) {
    super(
      dinaService,
      authorizationService,
      Optional.of(auditService),
      new DinaMapper<>(AgentIdentifierTypeDto.class),
      AgentIdentifierTypeDto.class,
      AgentIdentifierType.class,
      null,
      null,
      props, objMapper);
    this.authenticatedUser = authenticatedUser.orElse(null);
  }

  @Override
  public <S extends AgentIdentifierTypeDto> S create(S resource) {
    if (authenticatedUser != null) {
      resource.setCreatedBy(authenticatedUser.getUsername());
    }
    return super.create(resource);
  }
}
