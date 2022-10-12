package ca.gc.aafc.agent.api.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Getter
@RequiredArgsConstructor
@Builder
public class Identifier {
  
  public enum IdentifierType {
    ORCID,
    WIKIDATA;
  }

  private final IdentifierType type;

  /**
   * uri are url for Orcid and Wikidata, but it may change for a more generic term.
   */
  @URL
  private final String uri;

}
