package ca.gc.aafc.agent.api.repository;

import java.util.Arrays;

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

  private DinaAuthenticatedUser authenticatedUser;

  public AgentResourceRepository(
    JpaDtoRepository dtoRepository,
    SimpleFilterHandler simpleFilterHandler,
    RsqlFilterHandler rsqlFilterHandler,
    JpaMetaInformationProvider metaInformationProvider,
    DinaAuthenticatedUser authenticatedUser
  ) {
    super(
      AgentDto.class,
      dtoRepository,
      Arrays.asList(simpleFilterHandler, rsqlFilterHandler),
      metaInformationProvider
    );
    this.authenticatedUser = authenticatedUser;
  }

  @Override
  public <S extends AgentDto> S create(S resource) {
    resource.setCreatedBy(authenticatedUser.getUsername());
    return super.create(resource);
  }

}
