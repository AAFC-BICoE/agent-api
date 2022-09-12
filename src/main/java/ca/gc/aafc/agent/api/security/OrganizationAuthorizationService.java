package ca.gc.aafc.agent.api.security;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import ca.gc.aafc.dina.security.PermissionAuthorizationService;

@Service
public class OrganizationAuthorizationService extends PermissionAuthorizationService {

  @Override
  @PreAuthorize("hasMinimumDinaRole(@currentUser, 'GUEST')")
  public void authorizeCreate(Object entity) {
  }

  @Override
  public void authorizeRead(Object entity) {

  }

  @Override
  @PreAuthorize("hasMinimumDinaRole(@currentUser, 'SUPER_USER')")
  public void authorizeUpdate(Object entity) {
  }

  @Override
  @PreAuthorize("hasMinimumDinaRole(@currentUser, 'SUPER_USER')")
  public void authorizeDelete(Object entity) {
  }

  @Override
  public String getName() {
    return "OrganizationAuthorizationService";
  }

}
