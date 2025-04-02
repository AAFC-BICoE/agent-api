package ca.gc.aafc.agent.api;

import java.util.UUID;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.Link;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;

import java.util.Properties;

@SpringBootTest(classes = AgentModuleApiLauncher.class)
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.yml")
@Transactional
@ContextConfiguration(initializers = { PostgresTestContainerInitializer.class })
@Import(BaseIntegrationTest.CollectionModuleTestConfiguration.class)
public abstract class BaseIntegrationTest {

  public static UUID extractUUIDFromLink(Link link) {

    if(link == null) {
      return null;
    }
    return UUID.fromString(
      StringUtils.substringAfterLast(link.getHref(), "/"));
  }

  @TestConfiguration
  public static class CollectionModuleTestConfiguration {
    @Bean
    public BuildProperties buildProperties() {
      Properties props = new Properties();
      props.setProperty("version", "agent-module-version");
      return new BuildProperties(props);
    }
  }

}
