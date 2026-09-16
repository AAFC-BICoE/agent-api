package ca.gc.aafc.agent.api.repository;

import org.apache.commons.lang3.StringUtils;
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

import ca.gc.aafc.agent.api.dto.AgentControlledVocabularyItemDto;
import ca.gc.aafc.agent.api.entities.AgentControlledVocabulary;
import ca.gc.aafc.agent.api.entities.AgentControlledVocabularyItem;
import ca.gc.aafc.agent.api.mapper.AgentControlledVocabularyItemMapper;
import ca.gc.aafc.agent.api.service.AgentControlledVocabularyItemService;
import ca.gc.aafc.agent.api.service.AgentControlledVocabularyService;
import ca.gc.aafc.dina.exception.ConflictException;
import ca.gc.aafc.dina.exception.ResourceGoneException;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import ca.gc.aafc.dina.exception.ResourcesGoneException;
import ca.gc.aafc.dina.exception.ResourcesNotFoundException;
import ca.gc.aafc.dina.jsonapi.JsonApiBulkDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiBulkResourceIdentifierDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.repository.DinaRepositoryV2;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.security.TextHtmlSanitizer;
import ca.gc.aafc.dina.security.auth.SuperUserInGroupCUDAuthorizationService;
import ca.gc.aafc.dina.service.AuditService;
import ca.gc.aafc.dina.util.UUIDHelper;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;

@RestController
@RequestMapping(value = "${dina.apiPrefix:}", produces = JSON_API_VALUE)
public class AgentControlledVocabularyItemRepository extends DinaRepositoryV2<AgentControlledVocabularyItemDto, AgentControlledVocabularyItem> {

  // Bean does not exist with keycloak disabled.
  private final DinaAuthenticatedUser authenticatedUser;

  private final AgentControlledVocabularyService controlledVocabularyService;
  private final AgentControlledVocabularyItemService controlledVocabularyItemService;

  public AgentControlledVocabularyItemRepository(
    @NonNull AgentControlledVocabularyItemService controlledVocabularyItemService,
    @NonNull AgentControlledVocabularyService controlledVocabularyService,
    @NonNull SuperUserInGroupCUDAuthorizationService authorizationService,
    Optional<DinaAuthenticatedUser> authenticatedUser,
    @NonNull BuildProperties props,
    @NonNull AuditService auditService,
    @NonNull ObjectMapper objMapper
  ) {
    super(
      controlledVocabularyItemService, authorizationService,
      Optional.of(auditService),
      AgentControlledVocabularyItemMapper.INSTANCE,
      AgentControlledVocabularyItemDto.class,
      AgentControlledVocabularyItem.class,
      props, objMapper);
    this.authenticatedUser = authenticatedUser.orElse(null);
    this.controlledVocabularyItemService = controlledVocabularyItemService;
    this.controlledVocabularyService = controlledVocabularyService;
  }

  @Override
  protected Link generateLinkToResource(AgentControlledVocabularyItemDto dto) {
    try {
      return linkTo(methodOn(AgentControlledVocabularyItemRepository.class).onFindOne(dto.getUuid().toString(), null)).withSelfRel();
    } catch (ResourceNotFoundException | ResourceGoneException e) {
      throw new RuntimeException(e);
    }
  }

  @GetMapping(AgentControlledVocabularyItemDto.TYPENAME + "/{idOrKey}")
  public ResponseEntity<RepresentationModel<?>> onFindOne(@PathVariable String idOrKey, HttpServletRequest req)
      throws ResourceNotFoundException, ResourceGoneException {

    if (StringUtils.isBlank(idOrKey)) {
      throw ResourceNotFoundException.create(AgentControlledVocabularyItemDto.TYPENAME, "");
    }

    Optional<UUID> id = UUIDHelper.toUUID(idOrKey);
    if (id.isPresent()) {
      return handleFindOne(id.get(), req);
    }


    // key is always a compound key vocabKey.itemKey[.dinaComponent]
    String[] keyParts = StringUtils.split(idOrKey, ".");

    if (keyParts.length == 2 || keyParts.length == 3) {
      AgentControlledVocabulary vocab = controlledVocabularyService.findOneByKey(keyParts[0]);
      if (vocab != null) {
        AgentControlledVocabularyItem item = controlledVocabularyItemService.findOneByKey(keyParts[1], vocab.getUuid(),
          keyParts.length == 3 ? keyParts[2] : null);
        if (item != null) {
          return handleFindOne(item.getUuid(), req);
        }
      }
    }
    throw ResourceNotFoundException.create(AgentControlledVocabularyItemDto.TYPENAME,
      TextHtmlSanitizer.sanitizeText(idOrKey));
  }

  @PostMapping(path = AgentControlledVocabularyItemDto.TYPENAME + "/" + DinaRepositoryV2.JSON_API_BULK_LOAD_PATH,
    consumes = JSON_API_BULK)
  public ResponseEntity<RepresentationModel<?>> onBulkLoad(@RequestBody
                                                           JsonApiBulkResourceIdentifierDocument jsonApiBulkDocument,
                                                           HttpServletRequest req)
      throws ResourcesNotFoundException, ResourcesGoneException {
    return handleBulkLoad(jsonApiBulkDocument, req);
  }

  @GetMapping(AgentControlledVocabularyItemDto.TYPENAME)
  public ResponseEntity<RepresentationModel<?>> onFindAll(HttpServletRequest req) {
    return handleFindAll(req);
  }

  @PostMapping(path = AgentControlledVocabularyItemDto.TYPENAME + "/" + DinaRepositoryV2.JSON_API_BULK_PATH, consumes = JSON_API_BULK)
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onBulkCreate(@RequestBody
                                                             JsonApiBulkDocument jsonApiBulkDocument) {
    return handleBulkCreate(jsonApiBulkDocument, dto -> {
      if (authenticatedUser != null) {
        dto.setCreatedBy(authenticatedUser.getUsername());
      }
    });
  }

  @PostMapping(AgentControlledVocabularyItemDto.TYPENAME)
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onCreate(@RequestBody JsonApiDocument postedDocument) {

    return handleCreate(postedDocument, dto -> {
      if (authenticatedUser != null) {
        dto.setCreatedBy(authenticatedUser.getUsername());
      }
    });
  }

  @PatchMapping(path = AgentControlledVocabularyItemDto.TYPENAME + "/" + DinaRepositoryV2.JSON_API_BULK_PATH, consumes = JSON_API_BULK)
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onBulkUpdate(@RequestBody JsonApiBulkDocument jsonApiBulkDocument)
      throws ResourceNotFoundException, ResourceGoneException, ConflictException {
    return handleBulkUpdate(jsonApiBulkDocument);
  }

  @PatchMapping(AgentControlledVocabularyItemDto.TYPENAME + "/{id}")
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onUpdate(@RequestBody JsonApiDocument partialPatchDto,
                                                         @PathVariable UUID id)
      throws ResourceNotFoundException, ResourceGoneException, ConflictException {
    return handleUpdate(partialPatchDto, id);
  }

  @DeleteMapping(path = AgentControlledVocabularyItemDto.TYPENAME + "/" + DinaRepositoryV2.JSON_API_BULK_PATH, consumes = JSON_API_BULK)
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onBulkDelete(@RequestBody
                                                             JsonApiBulkResourceIdentifierDocument jsonApiBulkDocument)
      throws ResourceNotFoundException, ResourceGoneException {
    return handleBulkDelete(jsonApiBulkDocument);
  }

  @DeleteMapping(AgentControlledVocabularyItemDto.TYPENAME + "/{id}")
  @Transactional
  public ResponseEntity<RepresentationModel<?>> onDelete(@PathVariable UUID id) throws ResourceNotFoundException, ResourceGoneException {
    return handleDelete(id);
  }
}
