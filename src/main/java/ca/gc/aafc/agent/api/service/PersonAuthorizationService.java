package ca.gc.aafc.agent.api.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import ca.gc.aafc.dina.service.DinaAuthorizationService;

@Service
public class PersonAuthorizationService implements DinaAuthorizationService {

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
    // TODO Auto-generated method stub
    
  }



}
