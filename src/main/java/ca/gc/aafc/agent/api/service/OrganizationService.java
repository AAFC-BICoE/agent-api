package ca.gc.aafc.agent.api.service;

import ca.gc.aafc.agent.api.dto.OrganizationDto;
import ca.gc.aafc.agent.api.entities.Organization;
import ca.gc.aafc.agent.api.entities.OrganizationNameTranslation;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.search.messaging.producer.MessageProducer;
import ca.gc.aafc.dina.service.MessageProducingService;
import lombok.NonNull;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrganizationService extends MessageProducingService<Organization> {

  private final BaseDAO dao;

  public OrganizationService(
    @NonNull BaseDAO baseDAO,
    @NonNull SmartValidator smartValidator,
    MessageProducer producer
  ) {
    super(baseDAO, smartValidator, OrganizationDto.TYPENAME, producer);
    this.dao = baseDAO;
  }

  @Override
  protected void preCreate(Organization entity) {
    //Give new Organization UUID
    entity.setUuid(UUID.randomUUID());
    //Link name translations to the Organization before cascade
    if (CollectionUtils.isNotEmpty(entity.getNames())) {
      entity.getNames().forEach(trans -> trans.setOrganization(entity));
    }
  }

  @Override
  protected void preUpdate(Organization entity) {
    List<OrganizationNameTranslation> incomingTranslations = entity.getNames();
    entity.setNames(null);
    Map<String, OrganizationNameTranslation> oldTranslations = fetchTranslations(entity);

    if (CollectionUtils.isNotEmpty(incomingTranslations)) {
      Map<String, OrganizationNameTranslation> newTranslations =
        mapTranslationsToPersist(entity, incomingTranslations, oldTranslations);
      removeUnusedTranslations(oldTranslations, newTranslations);
      entity.setNames(new ArrayList<>(newTranslations.values()));
    } else {
      oldTranslations.values().forEach(dao::delete);
    }
  }

  /**
   * Returns translations for a given entity.
   *
   * @param entity - a given entity.
   * @return Returns translations for a given entity.
   */
  private Map<String, OrganizationNameTranslation> fetchTranslations(@NonNull Organization entity) {
    return this.findAll(
      OrganizationNameTranslation.class,
      (cb, root) -> new Predicate[]{cb.equal(root.get("organization"), entity)},
      null, 0, Integer.MAX_VALUE).stream().collect(
      Collectors.toMap(OrganizationNameTranslation::getLanguageCode, Function.identity()));
  }

  /**
   * Returns a Map of translations per language. the returned translations are backed by the
   * database if they already exist, or they are linked to the given entity.
   *
   * @param entity       - owner of the translations
   * @param translations - translations to map
   * @param persistedMap - current database backed translations
   * @return Returns a Map of translations per language
   */
  private static Map<String, OrganizationNameTranslation> mapTranslationsToPersist(
    @NonNull Organization entity,
    @NonNull List<OrganizationNameTranslation> translations,
    @NonNull Map<String, OrganizationNameTranslation> persistedMap
  ) {
    return translations.stream()
      .map(nameTranslation -> linkTranslation(entity, persistedMap, nameTranslation))
      .collect(Collectors.toMap(OrganizationNameTranslation::getLanguageCode, Function.identity()));
  }

  /**
   * Removes translations from the database that are no longer wanted.
   *
   * @param oldTranslations     - old set of translations
   * @param currentTranslations - translations to keep
   */
  private void removeUnusedTranslations(
    @NonNull Map<String, OrganizationNameTranslation> oldTranslations,
    @NonNull Map<String, OrganizationNameTranslation> currentTranslations
  ) {
    oldTranslations.values().forEach(translation -> {
      if (!currentTranslations.containsKey(translation.getLanguageCode())) {
        dao.delete(translation);
      }
    });
  }

  /**
   * Returns a data base backed representation of a given translation from a given map if it exists.
   * If the given translation does not exist in the given map  the given translation is returned
   * after being linked to the given entity.
   *
   * @param entity       - owner of the translations
   * @param persistedMap - current database backed translations
   * @param translation  - translation to link
   * @return Returns a data base backed representation of a given translation
   */
  private static OrganizationNameTranslation linkTranslation(
    @NonNull Organization entity,
    @NonNull Map<String, OrganizationNameTranslation> persistedMap,
    @NonNull OrganizationNameTranslation translation
  ) {
    String language = translation.getLanguageCode();
    if (persistedMap.containsKey(language)) {
      OrganizationNameTranslation persisted = persistedMap.get(language);
      if (!StringUtils.equalsIgnoreCase(persisted.getName(), translation.getName())) {
        persisted.setName(translation.getName());
      }
      return persisted;
    } else {
      translation.setOrganization(entity);
      return translation;
    }
  }

}
