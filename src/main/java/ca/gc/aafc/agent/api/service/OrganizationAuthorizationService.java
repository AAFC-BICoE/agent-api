package ca.gc.aafc.agent.api.service;

import ca.gc.aafc.dina.security.PermissionAuthorizationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class OrganizationAuthorizationService extends PermissionAuthorizationService {

  @Override
  @PreAuthorize("hasDinaRole(@currentUser, 'COLLECTION_MANAGER')")
  public void authorizeUpdate(Object entity) {
  }

  @Override
  @PreAuthorize("hasDinaRole(@currentUser, 'COLLECTION_MANAGER')")
  public void authorizeDelete(Object entity) {
  }

  @Override
  public void authorizeCreate(Object entity) {
  }

}
