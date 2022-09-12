package ca.gc.aafc.agent.api.repository;

import ca.gc.aafc.agent.api.dto.PersonDto;
import ca.gc.aafc.agent.api.entities.Person;
import ca.gc.aafc.agent.api.security.UpdateDeleteSuperUserOnly;
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
public class PersonRepository extends DinaRepository<PersonDto, Person> {

  // Bean does not exist with keycloak disabled.
  private Optional<DinaAuthenticatedUser> authenticatedUser;

  public PersonRepository(
    @NonNull DinaService<Person> dinaService,
    @NonNull UpdateDeleteSuperUserOnly authorizationService,
    Optional<DinaAuthenticatedUser> authenticatedUser,
    @NonNull BuildProperties props,
    @NonNull AuditService auditService
  ) {
    super(
      dinaService,
      authorizationService,
      Optional.of(auditService),
      new DinaMapper<>(PersonDto.class),
      PersonDto.class,
      Person.class,
      null,
      null,
      props);
    this.authenticatedUser = authenticatedUser;
  }

  @Override
  public <S extends PersonDto> S create(S resource) {
    if (authenticatedUser.isPresent()) {
      resource.setCreatedBy(authenticatedUser.get().getUsername());
    }
    return super.create(resource);
  }

}
