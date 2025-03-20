package ca.gc.aafc.agent.api.repository;

import org.springframework.boot.info.BuildProperties;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.gc.aafc.agent.api.dto.OrganizationDto;
import ca.gc.aafc.agent.api.entities.Organization;
import ca.gc.aafc.agent.api.mapper.OrganizationMapper;
import ca.gc.aafc.agent.api.security.UpdateDeleteSuperUserOnly;
import ca.gc.aafc.dina.exception.ResourceGoneException;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.repository.DinaRepositoryV2;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.service.AuditService;
import ca.gc.aafc.dina.service.DinaService;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import lombok.NonNull;

@RestController
@RequestMapping(value = "/api/v1", produces = JSON_API_VALUE)
public class OrganizationRepositoryV2 extends DinaRepositoryV2<OrganizationDto, Organization> {

  private static final String TYPE = OrganizationDto.TYPENAME + "v2";

  // Bean does not exist with keycloak disabled.
  private final DinaAuthenticatedUser authenticatedUser;

  public OrganizationRepositoryV2(
    @NonNull DinaService<Organization> dinaService,
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
      OrganizationMapper.INSTANCE,
      OrganizationDto.class,
      Organization.class,
      props, objMapper);

    this.authenticatedUser = authenticatedUser.orElse(null);
  }

  @Override
  protected Link generateLinkToResource(OrganizationDto dto) {
    try {
      return linkTo(methodOn(OrganizationRepositoryV2.class).onFindOne(dto.getUuid(), null)).withSelfRel();
    } catch (ResourceNotFoundException | ResourceGoneException e) {
      throw new RuntimeException(e);
    }
  }

  @GetMapping(TYPE + "/{id}")
  public ResponseEntity<RepresentationModel<?>> onFindOne(@PathVariable UUID id, HttpServletRequest req)
      throws ResourceNotFoundException, ResourceGoneException {
    return handleFindOne(id, req);
  }

  @GetMapping(TYPE)
  public ResponseEntity<RepresentationModel<?>> onFindAll(HttpServletRequest req) {
    return handleFindAll(req);
  }

  @PostMapping(TYPE)
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onCreate(@RequestBody JsonApiDocument postedDocument)
      throws ResourceNotFoundException, ResourceGoneException {

    return handleCreate(postedDocument, dto -> {
      if (authenticatedUser != null) {
        dto.setCreatedBy(authenticatedUser.getUsername());
      }
    });
  }

  @PatchMapping(TYPE + "/{id}")
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onUpdate(@RequestBody JsonApiDocument partialPatchDto,
                                                         @PathVariable UUID id) throws ResourceNotFoundException, ResourceGoneException {
    return handleUpdate(partialPatchDto, id);
  }

  @DeleteMapping(TYPE + "/{id}")
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onDelete(@PathVariable UUID id) throws ResourceNotFoundException {
    return handleDelete(id);
  }
}
