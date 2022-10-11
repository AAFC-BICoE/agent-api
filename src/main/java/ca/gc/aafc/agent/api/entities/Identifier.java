package ca.gc.aafc.agent.api.entities;

import java.net.URI;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class Identifier {
  
  public enum IdentifierType {
    ORCID,
    WIKIDATA;
  }

  private final IdentifierType type;

  private final URI uri;

}
