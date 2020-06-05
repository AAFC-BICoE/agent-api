package ca.gc.aafc.agent.api.repository;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ca.gc.aafc.agent.api.dto.AgentDto;
import ca.gc.aafc.dina.filter.RsqlFilterHandler;
import ca.gc.aafc.dina.filter.SimpleFilterHandler;
import ca.gc.aafc.dina.repository.JpaDtoRepository;
import ca.gc.aafc.dina.repository.JpaResourceRepository;
import ca.gc.aafc.dina.repository.meta.JpaMetaInformationProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;

@Repository
public class AgentResourceRepository extends JpaResourceRepository<AgentDto> {

  @Autowired(required = false) // Bean does not exist with keycloak disabled.
  private DinaAuthenticatedUser authenticatedUser;

  public AgentResourceRepository(
    JpaDtoRepository dtoRepository,
    SimpleFilterHandler simpleFilterHandler,
    RsqlFilterHandler rsqlFilterHandler,
    JpaMetaInformationProvider metaInformationProvider
  ) {
    super(
      AgentDto.class,
      dtoRepository,
      Arrays.asList(simpleFilterHandler, rsqlFilterHandler),
      metaInformationProvider
    );
  }

  @Override
  public <S extends AgentDto> S create(S resource) {
    if (authenticatedUser != null) {
      resource.setCreatedBy(authenticatedUser.getUsername());
    }
    return super.create(resource);
  }

}
