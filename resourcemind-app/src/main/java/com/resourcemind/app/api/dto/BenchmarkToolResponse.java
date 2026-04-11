package com.resourcemind.app.api.dto;

public record BenchmarkToolResponse(
		String useCaseType,
		String workloadProfile,
		int standardAllocationGpus,
		String notes
) {
}
