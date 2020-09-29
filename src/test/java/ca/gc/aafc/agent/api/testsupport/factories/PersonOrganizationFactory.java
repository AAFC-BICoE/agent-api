package ca.gc.aafc.agent.api.testsupport.factories;

import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;
import ca.gc.aafc.agent.api.entities.PersonOrganization;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

public class PersonOrganizationFactory implements TestableEntityFactory<PersonOrganization> {
  
  private static final ZoneId MTL_TZ = ZoneId.of("America/Montreal");
  private static final ZonedDateTime TEST_ZONED_DT = ZonedDateTime.of(2019, 1, 2, 3, 4, 5, 0, MTL_TZ);
  private static final OffsetDateTime TEST_OFFSET_DT = TEST_ZONED_DT.toOffsetDateTime();  

  @Override
  public PersonOrganization getEntityInstance() {
    return newPersonOrganization().build();
  }
  /**
   * Static method that can be called to return a configured builder that can be further customized
   * to return the actual entity object, call the .build() method on a builder.
   * 
   * @return Pre-configured builder with all mandatory fields set
   */
  public static PersonOrganization.PersonOrganizationBuilder newPersonOrganization() {
    return PersonOrganization.builder()
      .uuid(UUID.randomUUID())
      .person(PersonFactory.newPerson()
        .build())
      .organization(OrganizationFactory.newOrganization()
        .build())
      .createdBy(RandomStringUtils.random(4))
      .createdOn(TEST_OFFSET_DT);
   } 
  
  /**
   * A utility method to create a list of qty number of PersonOrganizations with
   * no configuration.
   * 
   * @param qty The number of PersonOrganizations populated in the list
   * @return List of PersonOrganization
   */
  public static List<PersonOrganization> newListOf(int qty) {
    return newListOf(qty, null);
  }

  /**
   * A utility method to create a list of qty number of PersonOrganization with an incrementing attribute
   * based on the configuration argument. An example of configuration would be the functional
   * interface (bldr, index) -> bldr.name(" string" + index)
   * 
   * @param qty           The number of PersonOrganization that is populated in the list.
   * @param configuration the function to apply, usually to differentiate the different entities in
   *                      the list.
   * @return List of PersonOrganization
   */
  public static List<PersonOrganization> newListOf(int qty,
      BiFunction<PersonOrganization.PersonOrganizationBuilder, Integer, PersonOrganization.PersonOrganizationBuilder> configuration) {
    
    return TestableEntityFactory.newEntity(qty, PersonOrganizationFactory::newPersonOrganization, configuration,
        PersonOrganization.PersonOrganizationBuilder::build);
  }
  

}
