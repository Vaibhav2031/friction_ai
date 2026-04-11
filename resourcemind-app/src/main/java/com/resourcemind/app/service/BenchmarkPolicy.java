package com.resourcemind.app.service;

import com.resourcemind.app.api.error.BadRequestException;
import org.springframework.util.StringUtils;

public final class BenchmarkPolicy {

	public static final String TYPE_AI_WEB_APP = "AI_WEB_APP";

	private BenchmarkPolicy() {
	}

	public static void validateBenchmarkQuery(String useCaseType, String workloadProfile) {
		if (!StringUtils.hasText(useCaseType)) {
			throw new BadRequestException("Query parameter 'type' is required.");
		}
		if (TYPE_AI_WEB_APP.equalsIgnoreCase(useCaseType.strip())
				&& !StringUtils.hasText(workloadProfile)) {
			throw new BadRequestException(
					"Query parameter 'profile' is required for use case type AI_WEB_APP."
			);
		}
	}
}
