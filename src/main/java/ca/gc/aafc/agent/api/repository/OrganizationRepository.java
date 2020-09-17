package ca.gc.aafc.agent.api.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import ca.gc.aafc.agent.api.dto.OrganizationDto;
import ca.gc.aafc.agent.api.entities.Organization;
import ca.gc.aafc.agent.api.service.OrganizationAuthorizationService;
import ca.gc.aafc.dina.filter.DinaFilterResolver;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.service.DinaService;
import lombok.NonNull;

@Repository
public class OrganizationRepository extends DinaRepository<OrganizationDto, Organization> {

  private Optional<DinaAuthenticatedUser> authenticatedUser;
  
  public OrganizationRepository(
    @NonNull DinaService<Organization> dinaService,
    @NonNull OrganizationAuthorizationService authorizationService,
    @NonNull DinaFilterResolver filterResolver,
    Optional<DinaAuthenticatedUser> authenticatedUser
  ) {
    super(
      dinaService,
      Optional.of(authorizationService),
      Optional.empty(), //no auditing for now
      new DinaMapper<>(OrganizationDto.class),
      OrganizationDto.class,
      Organization.class,
      filterResolver);
    this.authenticatedUser = authenticatedUser;
  }

}
