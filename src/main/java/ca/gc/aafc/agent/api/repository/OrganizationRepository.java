package ca.gc.aafc.agent.api.repository;

import ca.gc.aafc.agent.api.dto.OrganizationDto;
import ca.gc.aafc.agent.api.entities.Organization;
import ca.gc.aafc.agent.api.service.OrganizationAuthorizationService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.service.AuditService;
import ca.gc.aafc.dina.service.DinaService;
import lombok.NonNull;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class OrganizationRepository extends DinaRepository<OrganizationDto, Organization> {

  private Optional<DinaAuthenticatedUser> authenticatedUser;

  public OrganizationRepository(
    @NonNull DinaService<Organization> dinaService,
    @NonNull OrganizationAuthorizationService authorizationService,
    Optional<DinaAuthenticatedUser> authenticatedUser,
    @NonNull BuildProperties props,
    @NonNull AuditService auditService
  ) {
    super(
      dinaService,
      authorizationService,
      Optional.of(auditService),
      new DinaMapper<>(OrganizationDto.class),
      OrganizationDto.class,
      Organization.class,
      null,
      null,
      props);
    this.authenticatedUser = authenticatedUser;
  }

  @Override
  public <S extends OrganizationDto> S create(S resource) {
    if (authenticatedUser.isPresent()) {
      resource.setCreatedBy(authenticatedUser.get().getUsername());
    }
    return super.create(resource);
  }

  @Override
  public <S extends OrganizationDto> S save(S resource) {
    return super.save(resource);
  }
}
