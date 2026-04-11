package com.resourcemind.app.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateProposalRequest(
		@NotBlank @Size(max = 512) String title,
		@NotBlank String description,
		@NotBlank @Size(max = 64) String useCaseType,
		@Size(max = 64) String workloadProfile,
		@Size(max = 128) String useCaseKey,
		@NotNull @Min(0) Integer requestedGpus,
		@Size(max = 128) String agentCorrelationId
) {
}
