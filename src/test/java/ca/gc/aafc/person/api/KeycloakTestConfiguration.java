package ca.gc.aafc.person.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.gc.aafc.dina.security.DinaAuthenticatedUser;

/**
 * Configuration class used to provide a test instance of {@link DinaAuthenticatedUser}.
 * Keycloak should be disabled in order to use the configuration.
 */
@Configuration
public class KeycloakTestConfiguration {
  public static final String USER_NAME = "test_user";
  @Bean
  public DinaAuthenticatedUser dinaAuthenticatedUser() {
    DinaAuthenticatedUser testUser = DinaAuthenticatedUser.builder()
    .username(USER_NAME)
    .build();
    return testUser;
  }
}