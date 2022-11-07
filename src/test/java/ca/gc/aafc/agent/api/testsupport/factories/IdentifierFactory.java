package ca.gc.aafc.agent.api.testsupport.factories;

import ca.gc.aafc.agent.api.entities.Identifier;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;

public class IdentifierFactory implements TestableEntityFactory<Identifier> {

  public static final String TEST_NAMESPACE = "ORCID";

  @Override
  public Identifier getEntityInstance() {
    return newIdentifier().build();
  }

  /**
   * Static method that can be called to return a configured builder that can be
   * further customized to return the actual entity object, call the .build()
   * method on a builder.
   *
   * @return Pre-configured builder with all mandatory fields set
   */
  public static Identifier.IdentifierBuilder newIdentifier() {
    return Identifier
            .builder()
            .namespace(TEST_NAMESPACE)
            .value(TestableEntityFactory.generateRandomName(5));
  }
}