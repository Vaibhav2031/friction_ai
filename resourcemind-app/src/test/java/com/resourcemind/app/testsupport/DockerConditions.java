package com.resourcemind.app.testsupport;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.testcontainers.DockerClientFactory;

/**
 * Used by {@link org.junit.jupiter.api.condition.EnabledIf} so Spring does not start
 * (and Testcontainers does not fail the build) when Docker is not running.
 */
public final class DockerConditions {

	private DockerConditions() {
	}

	public static boolean isDockerAvailable() {
		if (!dockerSocketOrPipeLikelyPresent()) {
			return false;
		}
		try {
			return DockerClientFactory.instance().isDockerAvailable();
		}
		catch (Throwable ignored) {
			return false;
		}
	}

	private static boolean dockerSocketOrPipeLikelyPresent() {
		String os = System.getProperty("os.name", "").toLowerCase();
		if (os.contains("win")) {
			return Files.exists(Paths.get("\\\\.\\pipe\\docker_engine"));
		}
		if (Files.exists(Paths.get("/var/run/docker.sock"))) {
			return true;
		}
		Path colima = Paths.get(System.getProperty("user.home"), ".colima", "default", "docker.sock");
		return Files.exists(colima);
	}
}
