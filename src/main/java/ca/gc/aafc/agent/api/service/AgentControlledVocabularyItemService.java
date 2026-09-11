package ca.gc.aafc.agent.api.service;


import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import ca.gc.aafc.agent.api.entities.AgentControlledVocabularyItem;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.ControlledVocabularyItemService;
import ca.gc.aafc.dina.validation.ControlledVocabularyItemValidator;

@Service
public class AgentControlledVocabularyItemService extends ControlledVocabularyItemService<AgentControlledVocabularyItem> {

  public AgentControlledVocabularyItemService(BaseDAO baseDAO, SmartValidator smartValidator,
                                                    ControlledVocabularyItemValidator itemValidator) {
    super(baseDAO, smartValidator, AgentControlledVocabularyItem.class, itemValidator);
  }

}
