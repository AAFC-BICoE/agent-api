package ca.gc.aafc.agent.api.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import ca.gc.aafc.dina.security.PermissionAuthorizationService;

@Service
public class UpdateDeleteCollectionManagerOnly extends PermissionAuthorizationService {

  @Override
  @PreAuthorize("hasMinimumDinaRole(@currentUser, 'STUDENT')")
  public void authorizeCreate(Object entity) {
  }

  @Override
  @PreAuthorize("hasMinimumDinaRole(@currentUser, 'COLLECTION_MANAGER')")
  public void authorizeUpdate(Object entity) {
  }

  @Override
  @PreAuthorize("hasMinimumDinaRole(@currentUser, 'COLLECTION_MANAGER')")
  public void authorizeDelete(Object entity) {
  }

  @Override
  public String getName() {
    return "UpdateDeleteCollectionManagerOnly";
  }
}
