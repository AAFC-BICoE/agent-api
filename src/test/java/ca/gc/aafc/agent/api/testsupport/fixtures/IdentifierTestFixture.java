package ca.gc.aafc.agent.api.testsupport.fixtures;

import ca.gc.aafc.agent.api.dto.IdentifierDto;

public class IdentifierTestFixture {

  public static IdentifierDto newIdentifier() {
    IdentifierDto identifierDto = new IdentifierDto();
    identifierDto.setNamespace("ORCID");
    identifierDto.setValue("000-1234");
    return identifierDto;
  }
}
