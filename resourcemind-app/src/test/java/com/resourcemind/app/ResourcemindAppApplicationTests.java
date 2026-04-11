package com.resourcemind.app;

import com.resourcemind.app.testsupport.PostgresContainerTestConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(PostgresContainerTestConfiguration.class)
@EnabledIf("com.resourcemind.app.testsupport.DockerConditions#isDockerAvailable")
class ResourcemindAppApplicationTests {

	@Test
	void contextLoads() {
	}

}
