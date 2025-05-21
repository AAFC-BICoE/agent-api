package ca.gc.aafc.agent.api.repository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toedter.spring.hateoas.jsonapi.JsonApiModelBuilder;

import ca.gc.aafc.agent.api.dto.VocabularyDto;
import ca.gc.aafc.agent.api.service.VocabularyService;
import ca.gc.aafc.dina.repository.ReadOnlyDinaRepositoryV2;

import static com.toedter.spring.hateoas.jsonapi.JsonApiModelBuilder.jsonApiModel;
import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;

@RestController
@RequestMapping(value = "${dina.apiPrefix:}", produces = JSON_API_VALUE)
public class VocabularyRepository extends ReadOnlyDinaRepositoryV2<String, VocabularyDto> {

  protected VocabularyRepository(VocabularyService vocabularyService) {
    super(vocabularyService);
  }

  @GetMapping("vocabulary/{id}")
  public ResponseEntity<RepresentationModel<?>> handleFindOne(@PathVariable String id) {

    VocabularyDto dto = findOne(id);

    if (dto == null) {
      return ResponseEntity.notFound().build();
    }

    JsonApiModelBuilder builder = jsonApiModel().model(RepresentationModel.of(dto));

    return ResponseEntity.ok(builder.build());
  }

  @GetMapping("vocabulary")
  public ResponseEntity<RepresentationModel<?>> handleFindAll(HttpServletRequest req) {

    String queryString = StringUtils.isBlank(req.getQueryString()) ? "" :
      URLDecoder.decode(req.getQueryString(), StandardCharsets.UTF_8);

    List<VocabularyDto> dtos ;
    try {
      dtos = findAll(queryString);
    } catch (IllegalArgumentException iaEx) {
      return ResponseEntity.badRequest().build();
    }

    JsonApiModelBuilder builder = jsonApiModel().model(CollectionModel.of(dtos));

    return ResponseEntity.ok(builder.build());
  }

}
