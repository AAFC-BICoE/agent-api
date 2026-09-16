package ca.gc.aafc.agent.api.repository;

import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.gc.aafc.agent.api.AgentModuleApiLauncher;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.repository.MockMvcBasedRepository;

import java.util.Properties;

@SpringBootTest(classes = AgentModuleApiLauncher.class)
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.yml")
@ContextConfiguration(initializers = { PostgresTestContainerInitializer.class })
@Import(AgentModuleBaseRepositoryIT.AgentModuleTestConfiguration.class)
public abstract class AgentModuleBaseRepositoryIT extends MockMvcBasedRepository {

  protected AgentModuleBaseRepositoryIT(String baseUrl, ObjectMapper objMapper) {
    super(baseUrl, objMapper);
  }

  @TestConfiguration
  public static class AgentModuleTestConfiguration {
    @Bean
    public BuildProperties buildProperties() {
      Properties props = new Properties();
      props.setProperty("version", "agent-module-version");
      return new BuildProperties(props);
    }
  }
}
