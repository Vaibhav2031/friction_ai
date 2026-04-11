package com.resourcemind.app.api.dto;

public record ResourcesToolResponse(
		String poolName,
		int availableGpus,
		int totalGpus
) {
}
