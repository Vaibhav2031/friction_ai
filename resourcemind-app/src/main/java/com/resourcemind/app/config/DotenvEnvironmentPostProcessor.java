package com.resourcemind.app.config;

import io.github.cdimascio.dotenv.Dotenv;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

/**
 * Loads {@code .env} from the process working directory before the context refreshes.
 * Does not override existing OS environment variables or JVM properties.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {

	private static final String SOURCE_NAME = "dotenv";

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		Dotenv dotenv = Dotenv.configure().directory("./").ignoreIfMissing().load();
		Map<String, Object> map = new HashMap<>();
		dotenv.entries().forEach(e -> {
			String key = e.getKey();
			if (environment.getProperty(key) == null) {
				map.put(key, e.getValue());
			}
		});
		if (!map.isEmpty()) {
			environment.getPropertySources().addFirst(new MapPropertySource(SOURCE_NAME, map));
		}
	}

}
