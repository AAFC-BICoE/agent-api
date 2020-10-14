package ca.gc.aafc.agent.api.service;

import ca.gc.aafc.agent.api.entities.Organization;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DinaService;
import lombok.NonNull;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrganizationService extends DinaService<Organization> {

  public OrganizationService(@NonNull BaseDAO baseDAO) {
    super(baseDAO);
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

  }

}
