package ca.gc.aafc.agent.api.entities;

import jakarta.persistence.Entity;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import ca.gc.aafc.dina.entity.ControlledVocabulary;

@Entity(name = "controlled_vocabulary")
@SuperBuilder
@RequiredArgsConstructor
public class AgentControlledVocabulary extends ControlledVocabulary {

}
