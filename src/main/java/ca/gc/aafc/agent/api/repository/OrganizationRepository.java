package ca.gc.aafc.agent.api.repository;

import ca.gc.aafc.agent.api.dto.OrganizationDto;
import ca.gc.aafc.agent.api.entities.Organization;
import ca.gc.aafc.agent.api.service.OrganizationAuthorizationService;
import ca.gc.aafc.dina.filter.DinaFilterResolver;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.service.DinaService;
import io.crnk.core.exception.BadRequestException;
import lombok.NonNull;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class OrganizationRepository extends DinaRepository<OrganizationDto, Organization> {

  private Optional<DinaAuthenticatedUser> authenticatedUser;
  private final MessageSource messageSource;

  public OrganizationRepository(
    @NonNull DinaService<Organization> dinaService,
    @NonNull OrganizationAuthorizationService authorizationService,
    @NonNull DinaFilterResolver filterResolver,
    Optional<DinaAuthenticatedUser> authenticatedUser,
    MessageSource messageSource
  ) {
    super(
      dinaService,
      Optional.of(authorizationService),
      Optional.empty(), //no auditing for now
      new DinaMapper<>(OrganizationDto.class),
      OrganizationDto.class,
      Organization.class,
      filterResolver,
      null);
    this.authenticatedUser = authenticatedUser;
    this.messageSource = messageSource;
  }

  @Override
  public <S extends OrganizationDto> S create(S resource) {
    if (CollectionUtils.isEmpty(resource.getNames())) {
      throw new BadRequestException(messageSource.getMessage(
        "organization.constraint.required.name", null, LocaleContextHolder.getLocale()));
    }
    if (authenticatedUser.isPresent()) {
      resource.setCreatedBy(authenticatedUser.get().getUsername());
    }
    return super.create(resource);
  }

  @Override
  public <S extends OrganizationDto> S save(S resource) {
    if (CollectionUtils.isEmpty(resource.getNames())) {
      throw new BadRequestException(messageSource.getMessage(
        "organization.constraint.required.name", null, LocaleContextHolder.getLocale()));
    }
    return super.save(resource);
  }
}
