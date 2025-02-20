package ca.gc.aafc.agent.api.repository;

import org.springframework.boot.info.BuildProperties;
import org.springframework.hateoas.IanaLinkRelations;
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
import com.toedter.spring.hateoas.jsonapi.JsonApiModelBuilder;

import ca.gc.aafc.agent.api.dto.OrganizationDto;
import ca.gc.aafc.agent.api.entities.Organization;
import ca.gc.aafc.agent.api.mapper.OrganizationMapper;
import ca.gc.aafc.agent.api.security.UpdateDeleteSuperUserOnly;
import ca.gc.aafc.dina.dto.JsonApiDto;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.repository.DinaRepositoryV2;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.service.AuditService;
import ca.gc.aafc.dina.service.DinaService;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.util.Objects;
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

  @GetMapping(TYPE + "/{id}")
  public ResponseEntity<RepresentationModel<?>> handleFindOne(@PathVariable UUID id, HttpServletRequest req) throws
    ResourceNotFoundException {
    String queryString = decodeQueryString(req);

    JsonApiDto<OrganizationDto> jsonApiDto = getOne(id, queryString);
    if (jsonApiDto == null) {
      return ResponseEntity.notFound().build();
    }

    JsonApiModelBuilder builder = createJsonApiModelBuilder(jsonApiDto);

    return ResponseEntity.ok(builder.build());
  }

  @GetMapping(TYPE)
  public ResponseEntity<RepresentationModel<?>> handleFindAll(HttpServletRequest req) {
    String queryString = decodeQueryString(req);

    PagedResource<JsonApiDto<OrganizationDto>> dtos;
    try {
      dtos = getAll(queryString);
    } catch (IllegalArgumentException iaEx) {
      return ResponseEntity.badRequest().build();
    }

    JsonApiModelBuilder builder = createJsonApiModelBuilder(dtos);

    return ResponseEntity.ok(builder.build());
  }

  @PostMapping(TYPE)
  @Transactional
  public ResponseEntity<RepresentationModel<?>> handleCreate(@RequestBody
                                                             JsonApiDocument postedDocument)
    throws ResourceNotFoundException {

    if (postedDocument == null) {
      return ResponseEntity.badRequest().build();
    }

    UUID uuid = create(postedDocument, (dto) -> {
      if (authenticatedUser != null) {
        dto.setCreatedBy(authenticatedUser.getUsername());
      }
    });

    // reload dto
    JsonApiDto<OrganizationDto> jsonApiDto = getOne(uuid, null);
    if (jsonApiDto == null) {
      return ResponseEntity.notFound().build();
    }
    JsonApiModelBuilder builder = createJsonApiModelBuilder(jsonApiDto);

    builder.link(linkTo(methodOn(OrganizationRepositoryV2.class).handleFindOne(jsonApiDto.getDto().getUuid(), null)).withSelfRel());
    RepresentationModel<?> model = builder.build();

    URI uri = model.getRequiredLink(IanaLinkRelations.SELF).toUri();

    return ResponseEntity.created(uri).body(model);
  }

  @PatchMapping(TYPE + "/{id}")
  @Transactional
  public ResponseEntity<RepresentationModel<?>> handleUpdate(@RequestBody
                                                             JsonApiDocument partialPatchDto,
                                                             @PathVariable UUID id) throws ResourceNotFoundException {

    // Sanity check
    if (!Objects.equals(id, partialPatchDto.getId())) {
      return ResponseEntity.badRequest().build();
    }

    update(partialPatchDto);

    // reload dto
    JsonApiDto<OrganizationDto> jsonApiDto = getOne(partialPatchDto.getId(), null);
    if (jsonApiDto == null) {
      return ResponseEntity.notFound().build();
    }
    JsonApiModelBuilder builder = createJsonApiModelBuilder(jsonApiDto);

    return ResponseEntity.ok().body(builder.build());
  }

  @DeleteMapping(TYPE + "/{id}")
  @Transactional
  public ResponseEntity<RepresentationModel<?>> handleDelete(@PathVariable UUID id) throws ResourceNotFoundException {
    delete(id);
    return ResponseEntity.noContent().build();
  }
}
