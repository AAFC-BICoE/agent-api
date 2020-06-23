package ca.gc.aafc.person.api.repository;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import ca.gc.aafc.dina.filter.RsqlFilterHandler;
import ca.gc.aafc.dina.filter.SimpleFilterHandler;
import ca.gc.aafc.dina.repository.JpaDtoRepository;
import ca.gc.aafc.dina.repository.JpaResourceRepository;
import ca.gc.aafc.dina.repository.meta.JpaMetaInformationProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.person.api.dto.PersonDto;

@Repository
public class PersonResourceRepository extends JpaResourceRepository<PersonDto> {

  // Bean does not exist with keycloak disabled.
  private Optional<DinaAuthenticatedUser> authenticatedUser;

  public PersonResourceRepository(
    JpaDtoRepository dtoRepository,
    SimpleFilterHandler simpleFilterHandler,
    RsqlFilterHandler rsqlFilterHandler,
    JpaMetaInformationProvider metaInformationProvider,
    Optional<DinaAuthenticatedUser> authenticatedUser
  ) {
    super(
      PersonDto.class,
      dtoRepository,
      Arrays.asList(simpleFilterHandler, rsqlFilterHandler),
      metaInformationProvider
    );
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
