package ca.gc.aafc.agent.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import ca.gc.aafc.agent.api.dto.PersonDto;

import ca.gc.aafc.dina.messaging.DinaEventPublisher;
import ca.gc.aafc.dina.messaging.EntityChanged;
import ca.gc.aafc.dina.service.MessageProducingService;
import jakarta.persistence.criteria.Predicate;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import ca.gc.aafc.agent.api.entities.Person;
import ca.gc.aafc.dina.jpa.BaseDAO;
import lombok.NonNull;
import org.springframework.validation.SmartValidator;

@Service
public class PersonService extends MessageProducingService<Person> {

  public PersonService(@NonNull BaseDAO baseDAO, @NonNull SmartValidator smartValidator,
                       DinaEventPublisher<EntityChanged> eventPublisher) {
    super(baseDAO, smartValidator, PersonDto.TYPENAME, eventPublisher);
  }

  @Override
  protected void preCreate(Person entity) {

    // allow user provided uuid
    if (entity.getUuid() == null) {
      entity.setUuid(UUID.randomUUID());
    }
    normalizeStrings(entity);
    checkForPotentialDuplicate(entity, false);
  }

  @Override
  protected void preUpdate(Person entity) {
    normalizeStrings(entity);
    checkForPotentialDuplicate(entity, true);
  }

  private void normalizeStrings(Person entity) {
    entity.setFamilyNames(StringUtils.normalizeSpace(entity.getFamilyNames()));
    entity.setGivenNames(StringUtils.normalizeSpace(entity.getGivenNames()));
    entity.setDisplayName(StringUtils.normalizeSpace(entity.getDisplayName()));
    entity.setAliases(entity.getAliases() != null ?
        Stream.of(entity.getAliases()).map(StringUtils::normalizeSpace).toArray(String[]::new) :
        null);
  }

  /**
   * Checks for a potential duplicate Person based on given and family names,
   * unless duplicate names are explicitly allowed.
   *
   * @param person             the Person to validate
   * @param excludeCurrentUuid whether to exclude the Person's UUID from the
   *                           duplicate search
   * @throws IllegalStateException if a potential duplicate Person is found
   */
  private void checkForPotentialDuplicate(Person person, boolean excludeCurrentUuid) throws IllegalStateException {

    if (BooleanUtils.isTrue(person.getAllowDuplicateName()) || StringUtils.isBlank(person.getFamilyNames()) || StringUtils.isBlank(person.getGivenNames())) {
      return;
    }

    List<Person> duplicates = findAll(
        Person.class,
        (cb, root) -> {
          var predicates = new ArrayList<Predicate>();

          predicates.add(
              cb.equal(cb.lower(root.get("familyNames")),
                  person.getFamilyNames().toLowerCase()));
          predicates.add(
              cb.equal(cb.lower(root.get("givenNames")),
                  person.getGivenNames().toLowerCase()));

          if (excludeCurrentUuid && person.getUuid() != null) {
            predicates.add(cb.notEqual(root.get("uuid"), person.getUuid()));
          }

          return predicates.toArray(Predicate[]::new);
        },
        null,
        0,
        1);

    if (!duplicates.isEmpty()) {
      throw new IllegalStateException("Potential duplicate person found: " + duplicates.getFirst().getUuid());
    }

  }

}
