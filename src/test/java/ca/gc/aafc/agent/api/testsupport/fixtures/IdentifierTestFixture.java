package ca.gc.aafc.agent.api.testsupport.fixtures;

import ca.gc.aafc.agent.api.dto.IdentifierDto;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;

public class IdentifierTestFixture {

  public static IdentifierDto newIdentifier() {
    IdentifierDto identifierDto = new IdentifierDto();
    identifierDto.setNamespace("ORCID");
    identifierDto.setValue(TestableEntityFactory.generateRandomName(5));
    return identifierDto;
  }
}
