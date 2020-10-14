package ca.gc.aafc.agent.api.service;

import ca.gc.aafc.agent.api.entities.Organization;
import ca.gc.aafc.agent.api.entities.OrganizationNameTranslation;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DinaService;
import lombok.NonNull;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrganizationService extends DinaService<Organization> {

  private final BaseDAO dao;

  public OrganizationService(@NonNull BaseDAO baseDAO) {
    super(baseDAO);
    this.dao = baseDAO;
  }

  @Override
  protected void preCreate(Organization entity) {
    //Give new Organization UUID
    entity.setUuid(UUID.randomUUID());
    //Link name translations to the Organization before cascade
    if (CollectionUtils.isNotEmpty(entity.getNameTranslations())) {
      entity.getNameTranslations().forEach(trans -> trans.setOrganization(entity));
    }
  }

  @Override
  protected void preDelete(Organization entity) {

  }

  @Override
  protected void preUpdate(Organization entity) {
    List<OrganizationNameTranslation> translations = entity.getNameTranslations();
    entity.setNameTranslations(null);
    Map<String, OrganizationNameTranslation> oldTranslations = fetchTranslations(entity);

    if (CollectionUtils.isNotEmpty(translations)) {
      Map<String, OrganizationNameTranslation> newTranslations =
        mapTranslationsToPersist(entity, translations, oldTranslations);

      removeUnusedTranslations(oldTranslations, newTranslations);

      entity.setNameTranslations(new ArrayList<>(newTranslations.values()));
    } else {
      oldTranslations.values().forEach(dao::delete);
    }
  }

  private Map<String, OrganizationNameTranslation> fetchTranslations(@NonNull Organization entity) {
    return this.findAll(
      OrganizationNameTranslation.class,
      (cb, root) -> new Predicate[]{cb.equal(root.get("organization"), entity)},
      null, 0, 1000).stream().collect(
      Collectors.toMap(OrganizationNameTranslation::getLanguage, Function.identity()));
  }

  private static Map<String, OrganizationNameTranslation> mapTranslationsToPersist(
    @NonNull Organization entity,
    @NonNull List<OrganizationNameTranslation> translations,
    @NonNull Map<String, OrganizationNameTranslation> persistedTranslations
  ) {
    return translations.stream()
      .map(nameTranslation -> linkTranslation(entity, persistedTranslations, nameTranslation))
      .collect(Collectors.toMap(OrganizationNameTranslation::getLanguage, Function.identity()));
  }

  private void removeUnusedTranslations(
    @NonNull Map<String, OrganizationNameTranslation> oldTranslations,
    @NonNull Map<String, OrganizationNameTranslation> currentTranslations
  ) {
    oldTranslations.values().forEach(translation -> {
      if (!currentTranslations.containsKey(translation.getLanguage())) {
        dao.delete(translation);
      }
    });
  }

  private static OrganizationNameTranslation linkTranslation(
    @NonNull Organization entity,
    @NonNull Map<String, OrganizationNameTranslation> persistedMap,
    @NonNull OrganizationNameTranslation translation
  ) {
    String language = translation.getLanguage();
    if (persistedMap.containsKey(language)) {
      OrganizationNameTranslation persisted = persistedMap.get(language);
      if (!StringUtils.equalsIgnoreCase(persisted.getValue(), translation.getValue())) {
        persisted.setValue(translation.getValue());
      }
      return persisted;
    } else {
      translation.setOrganization(entity);
      return translation;
    }
  }

}
