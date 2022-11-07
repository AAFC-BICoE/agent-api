package ca.gc.aafc.agent.api.repository;

import ca.gc.aafc.agent.api.dto.IdentifierDto;
import ca.gc.aafc.agent.api.entities.Identifier;
import ca.gc.aafc.agent.api.security.UpdateDeleteSuperUserOnly;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.service.AuditService;
import ca.gc.aafc.dina.service.DinaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class IdentifierRepository extends DinaRepository<IdentifierDto, Identifier> {

  // Bean does not exist with keycloak disabled.
  private final DinaAuthenticatedUser authenticatedUser;

  public IdentifierRepository(
    @NonNull DinaService<Identifier> dinaService,
    @NonNull UpdateDeleteSuperUserOnly authorizationService,
    Optional<DinaAuthenticatedUser> authenticatedUser,
    @NonNull BuildProperties props,
    @NonNull AuditService auditService,
    @NonNull ObjectMapper objMapper
  ) {
    super(
      dinaService,
      authorizationService,
      Optional.of(auditService),
      new DinaMapper<>(IdentifierDto.class),
      IdentifierDto.class,
      Identifier.class,
      null,
      null,
      props, objMapper);
    this.authenticatedUser = authenticatedUser.orElse(null);
  }

  @Override
  public <S extends IdentifierDto> S create(S resource) {
    if (authenticatedUser != null) {
      resource.setCreatedBy(authenticatedUser.getUsername());
    }
    return super.create(resource);
  }

}
