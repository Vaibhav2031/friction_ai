package com.resourcemind.app.api.dto;

import com.resourcemind.app.domain.DecisionOutcome;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;

public record AgentDecisionRequest(
		@NotNull UUID proposalId,
		@NotNull DecisionOutcome outcome,
		Integer approvedGpus,
		String rationaleSummary,
		Map<String, Object> decisionContract,
		String idempotencyKey
) {
}
