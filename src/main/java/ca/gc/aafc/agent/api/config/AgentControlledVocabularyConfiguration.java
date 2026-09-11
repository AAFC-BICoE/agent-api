package ca.gc.aafc.agent.api.config;

import java.util.UUID;

public final class AgentControlledVocabularyConfiguration {

  public static final UUID IDENTIFIER_TYPE_VOCAB_UUID = UUID.fromString("bff566b9-4044-4e0c-a9cd-0ab22c6f0e5b");

  AgentControlledVocabularyConfiguration() {
    // no-op
  }

  public enum DinaComponent {
    PERSON;

    public static DinaComponent fromString(String s) {
      for (DinaComponent source : DinaComponent.values()) {
        if (source.name().equalsIgnoreCase(s)) {
          return source;
        }
      }
      return null;
    }
  }

}
