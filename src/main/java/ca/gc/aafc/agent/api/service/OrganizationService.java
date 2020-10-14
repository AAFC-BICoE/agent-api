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
    if (CollectionUtils.isNotEmpty(translations)) {
      entity.setNameTranslations(null);
      Map<String, OrganizationNameTranslation> persistedTranslations = this.findAll(
        OrganizationNameTranslation.class,
        (cb, root) -> new Predicate[]{cb.equal(root.get("organization"), entity)},
        null, 0, 1000).stream().collect(
        Collectors.toMap(OrganizationNameTranslation::getLanguage, Function.identity()));

      Map<String, OrganizationNameTranslation> toPersist = translations.stream()
        .map(nameTranslation -> {
          String language = nameTranslation.getLanguage();
          if (persistedTranslations.containsKey(language)) {
            OrganizationNameTranslation persisted = persistedTranslations.get(language);
            if (!StringUtils.equalsIgnoreCase(persisted.getValue(), nameTranslation.getValue())) {
              persisted.setValue(nameTranslation.getValue());
            }
            return persisted;
          } else {
            nameTranslation.setOrganization(entity);
            return nameTranslation;
          }
        })
        .collect(Collectors.toMap(OrganizationNameTranslation::getLanguage, Function.identity()));

      persistedTranslations.values().forEach(organizationNameTranslation -> {
        if (!toPersist.containsKey(organizationNameTranslation.getLanguage())) {
          dao.delete(organizationNameTranslation);
        }
      });

      entity.setNameTranslations(new ArrayList<>(toPersist.values()));
    }
  }

}
