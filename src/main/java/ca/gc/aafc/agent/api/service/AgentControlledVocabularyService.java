package ca.gc.aafc.agent.api.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import ca.gc.aafc.agent.api.entities.AgentControlledVocabulary;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.ControlledVocabularyService;

@Service
public class AgentControlledVocabularyService extends ControlledVocabularyService<AgentControlledVocabulary> {

  public AgentControlledVocabularyService(BaseDAO baseDAO,
                                               SmartValidator smartValidator) {
    super(baseDAO, smartValidator, AgentControlledVocabulary.class);
  }
}
